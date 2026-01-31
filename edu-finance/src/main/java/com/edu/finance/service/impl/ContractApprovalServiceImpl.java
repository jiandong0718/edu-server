package com.edu.finance.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ApprovalRecordDTO;
import com.edu.finance.domain.dto.ContractApprovalProcessDTO;
import com.edu.finance.domain.dto.ContractApprovalQueryDTO;
import com.edu.finance.domain.dto.ContractApprovalSubmitDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractApproval;
import com.edu.finance.domain.entity.ContractApprovalConfig;
import com.edu.finance.domain.entity.ContractApprovalFlow;
import com.edu.finance.domain.vo.ContractApprovalFlowVO;
import com.edu.finance.domain.vo.ContractApprovalVO;
import com.edu.finance.event.ContractApprovalEvent;
import com.edu.finance.mapper.ContractApprovalConfigMapper;
import com.edu.finance.mapper.ContractApprovalFlowMapper;
import com.edu.finance.mapper.ContractApprovalMapper;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.ContractApprovalService;
import com.edu.framework.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合同审批服务实现（增强版）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractApprovalServiceImpl extends ServiceImpl<ContractApprovalMapper, ContractApproval>
        implements ContractApprovalService {

    private final ContractApprovalMapper approvalMapper;
    private final ContractApprovalFlowMapper approvalFlowMapper;
    private final ContractMapper contractMapper;
    private final ContractApprovalConfigMapper approvalConfigMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitApproval(ContractApprovalSubmitDTO submitDTO) {
        // 验证合同是否存在
        Contract contract = contractMapper.selectById(submitDTO.getContractId());
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 检查是否有待审批的记录
        LambdaQueryWrapper<ContractApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractApproval::getContractId, submitDTO.getContractId())
                .eq(ContractApproval::getStatus, "pending");
        Long count = approvalMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该合同已有待审批记录，请勿重复提交");
        }

        // 根据合同金额和审批类型获取审批配置
        ContractApprovalConfig config = approvalConfigMapper.selectByTypeAndAmount(
                submitDTO.getApprovalType(), contract.getAmount());

        if (config == null) {
            throw new BusinessException("未找到匹配的审批配置，请联系管理员");
        }

        // 创建审批记录
        ContractApproval approval = new ContractApproval();
        approval.setContractId(submitDTO.getContractId());
        approval.setApprovalNo(generateApprovalNo());
        approval.setApprovalType(submitDTO.getApprovalType());
        approval.setStatus("pending");
        approval.setSubmitterId(SecurityContextHolder.getUserId());
        approval.setSubmitTime(LocalDateTime.now());
        approval.setSubmitReason(submitDTO.getSubmitReason());
        approval.setCurrentStep(1);
        approval.setTotalSteps(config.getApprovalLevels());

        approvalMapper.insert(approval);

        // 解析审批人配置并创建审批流程
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> approverList = (List<Map<String, Object>>) (List<?>) JSONUtil.toList(config.getApproverConfig(), Map.class);
        for (Map<String, Object> approverConfig : approverList) {
            Integer level = (Integer) approverConfig.get("level");
            String roleCode = (String) approverConfig.get("roleCode");
            String roleName = (String) approverConfig.get("roleName");

            // 根据角色获取审批人（这里简化处理，实际应该从用户角色表查询）
            Long approverId = getApproverByRole(roleCode);

            ContractApprovalFlow flow = new ContractApprovalFlow();
            flow.setApprovalId(approval.getId());
            flow.setStepNo(level);
            flow.setApproverId(approverId);
            flow.setApproverName(roleName);
            flow.setStatus(level == 1 ? "pending" : "waiting"); // 第一级为待审批，其他为等待中

            approvalFlowMapper.insert(flow);
        }

        // 发布审批提交事件
        Long firstApproverId = approverList.isEmpty() ? null :
                getApproverByRole((String) approverList.get(0).get("roleCode"));
        publishApprovalEvent(approval.getId(), contract.getId(), "submitted",
                firstApproverId, null, approval.getSubmitterId(),
                SecurityContextHolder.getUsername(), contract.getContractNo(), null);

        log.info("合同审批提交成功: approvalId={}, contractId={}, levels={}",
                approval.getId(), submitDTO.getContractId(), config.getApprovalLevels());
        return approval.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean processApproval(ContractApprovalProcessDTO processDTO) {
        // 查询审批记录
        ContractApproval approval = approvalMapper.selectById(processDTO.getApprovalId());
        if (approval == null) {
            throw new BusinessException("审批记录不存在");
        }

        if (!"pending".equals(approval.getStatus())) {
            throw new BusinessException("该审批已处理，无法重复操作");
        }

        Long currentUserId = SecurityContextHolder.getUserId();
        String currentUsername = SecurityContextHolder.getUsername();

        // 查询当前用户的审批流程
        LambdaQueryWrapper<ContractApprovalFlow> flowWrapper = new LambdaQueryWrapper<>();
        flowWrapper.eq(ContractApprovalFlow::getApprovalId, processDTO.getApprovalId())
                .eq(ContractApprovalFlow::getApproverId, currentUserId)
                .eq(ContractApprovalFlow::getStatus, "pending");
        ContractApprovalFlow flow = approvalFlowMapper.selectOne(flowWrapper);

        if (flow == null) {
            throw new BusinessException("您没有权限审批该合同");
        }

        // 获取合同信息
        Contract contract = contractMapper.selectById(approval.getContractId());

        // 更新审批流程
        flow.setStatus(processDTO.getResult());
        flow.setApproveTime(LocalDateTime.now());
        flow.setApproveRemark(processDTO.getApproveRemark());
        approvalFlowMapper.updateById(flow);

        String eventType;
        // 根据审批结果处理
        if ("approved".equals(processDTO.getResult())) {
            // 检查是否还有下一级审批
            if (flow.getStepNo() < approval.getTotalSteps()) {
                // 激活下一级审批
                LambdaQueryWrapper<ContractApprovalFlow> nextFlowWrapper = new LambdaQueryWrapper<>();
                nextFlowWrapper.eq(ContractApprovalFlow::getApprovalId, processDTO.getApprovalId())
                        .eq(ContractApprovalFlow::getStepNo, flow.getStepNo() + 1);
                ContractApprovalFlow nextFlow = approvalFlowMapper.selectOne(nextFlowWrapper);
                if (nextFlow != null) {
                    nextFlow.setStatus("pending");
                    approvalFlowMapper.updateById(nextFlow);

                    // 更新当前步骤
                    approval.setCurrentStep(flow.getStepNo() + 1);
                    approvalMapper.updateById(approval);

                    // 发布事件通知下一级审批人
                    publishApprovalEvent(approval.getId(), contract.getId(), "submitted",
                            nextFlow.getApproverId(), nextFlow.getApproverName(),
                            approval.getSubmitterId(), null, contract.getContractNo(), null);

                    log.info("审批通过，进入下一级: approvalId={}, currentStep={}, totalSteps={}",
                            processDTO.getApprovalId(), approval.getCurrentStep(), approval.getTotalSteps());
                    return true;
                }
            }

            // 所有审批都通过，更新审批记录状态
            approval.setStatus("approved");
            approval.setApproverId(currentUserId);
            approval.setApproveTime(LocalDateTime.now());
            approval.setApproveRemark(processDTO.getApproveRemark());
            approvalMapper.updateById(approval);

            // 更新合同状态
            if ("pending".equals(contract.getStatus())) {
                contract.setStatus("signed");
                contractMapper.updateById(contract);
            }

            eventType = "approved";

        } else if ("rejected".equals(processDTO.getResult())) {
            // 审批拒绝
            approval.setStatus("rejected");
            approval.setApproverId(currentUserId);
            approval.setApproveTime(LocalDateTime.now());
            approval.setApproveRemark(processDTO.getApproveRemark());
            approvalMapper.updateById(approval);

            // 将其他待审批的流程标记为跳过
            skipRemainingFlows(processDTO.getApprovalId());

            eventType = "rejected";

        } else if ("returned".equals(processDTO.getResult())) {
            // 审批退回
            approval.setStatus("returned");
            approval.setApproverId(currentUserId);
            approval.setApproveTime(LocalDateTime.now());
            approval.setApproveRemark(processDTO.getApproveRemark());
            approvalMapper.updateById(approval);

            // 将其他待审批的流程标记为跳过
            skipRemainingFlows(processDTO.getApprovalId());

            eventType = "returned";

        } else {
            throw new BusinessException("不支持的审批结果: " + processDTO.getResult());
        }

        // 发布审批事件
        publishApprovalEvent(approval.getId(), contract.getId(), eventType,
                currentUserId, currentUsername, approval.getSubmitterId(),
                null, contract.getContractNo(), processDTO.getApproveRemark());

        log.info("合同审批处理成功: approvalId={}, result={}", processDTO.getApprovalId(), processDTO.getResult());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelApproval(Long approvalId) {
        ContractApproval approval = approvalMapper.selectById(approvalId);
        if (approval == null) {
            throw new BusinessException("审批记录不存在");
        }

        if (!"pending".equals(approval.getStatus())) {
            throw new BusinessException("只有待审批状态才能撤销");
        }

        Long currentUserId = SecurityContextHolder.getUserId();
        if (!approval.getSubmitterId().equals(currentUserId)) {
            throw new BusinessException("只有提交人才能撤销审批");
        }

        // 更新审批状态
        approval.setStatus("cancelled");
        approvalMapper.updateById(approval);

        // 更新审批流程状态
        skipRemainingFlows(approvalId);

        // 获取合同信息
        Contract contract = contractMapper.selectById(approval.getContractId());

        // 发布撤销事件
        publishApprovalEvent(approvalId, contract.getId(), "cancelled",
                null, null, currentUserId, SecurityContextHolder.getUsername(),
                contract.getContractNo(), null);

        log.info("合同审批撤销成功: approvalId={}", approvalId);
        return true;
    }

    @Override
    public List<ContractApproval> getApprovalHistory(Long contractId) {
        return approvalMapper.selectByContractId(contractId);
    }

    @Override
    public List<ContractApprovalFlow> getApprovalFlow(Long approvalId) {
        return approvalFlowMapper.selectByApprovalId(approvalId);
    }

    @Override
    public List<ContractApproval> getPendingApprovals(Long approverId) {
        return approvalMapper.selectPendingList(approverId);
    }

    @Override
    public IPage<ContractApprovalVO> getPendingApprovalsPage(IPage<ContractApprovalVO> page, Long approverId) {
        // 这里需要在Mapper中实现分页查询
        // 简化处理，返回空页
        return page;
    }

    @Override
    public IPage<ContractApprovalVO> getApprovalPage(IPage<ContractApprovalVO> page, ContractApprovalQueryDTO queryDTO) {
        // 这里需要在Mapper中实现分页查询
        // 简化处理，返回空页
        return page;
    }

    @Override
    public ContractApprovalVO getApprovalDetail(Long approvalId) {
        ContractApproval approval = approvalMapper.selectById(approvalId);
        if (approval == null) {
            return null;
        }

        ContractApprovalVO vo = BeanUtil.copyProperties(approval, ContractApprovalVO.class);

        // 获取审批流程
        List<ContractApprovalFlow> flows = approvalFlowMapper.selectByApprovalId(approvalId);
        List<ContractApprovalFlowVO> flowVOs = flows.stream()
                .map(flow -> BeanUtil.copyProperties(flow, ContractApprovalFlowVO.class))
                .collect(Collectors.toList());
        vo.setFlowList(flowVOs);

        // 获取合同信息
        Contract contract = contractMapper.selectById(approval.getContractId());
        if (contract != null) {
            vo.setContractNo(contract.getContractNo());
            vo.setStudentName(contract.getStudentName());
            vo.setContractAmount(contract.getAmount());
        }

        return vo;
    }

    @Override
    public List<ApprovalRecordDTO> getApprovalTimeline(Long approvalId) {
        ContractApproval approval = approvalMapper.selectById(approvalId);
        if (approval == null) {
            return new ArrayList<>();
        }

        List<ApprovalRecordDTO> timeline = new ArrayList<>();

        // 添加提交记录
        ApprovalRecordDTO submitRecord = new ApprovalRecordDTO();
        submitRecord.setStepNo(0);
        submitRecord.setActionType("submit");
        submitRecord.setActionTypeName("提交审批");
        submitRecord.setOperatorId(approval.getSubmitterId());
        submitRecord.setOperatorName(approval.getSubmitterName());
        submitRecord.setOperateTime(approval.getSubmitTime());
        submitRecord.setRemark(approval.getSubmitReason());
        submitRecord.setStatus("completed");
        timeline.add(submitRecord);

        // 添加审批流程记录
        List<ContractApprovalFlow> flows = approvalFlowMapper.selectByApprovalId(approvalId);
        for (ContractApprovalFlow flow : flows) {
            ApprovalRecordDTO record = new ApprovalRecordDTO();
            record.setStepNo(flow.getStepNo());
            record.setOperatorId(flow.getApproverId());
            record.setOperatorName(flow.getApproverName());
            record.setOperateTime(flow.getApproveTime());
            record.setRemark(flow.getApproveRemark());

            if ("approved".equals(flow.getStatus())) {
                record.setActionType("approve");
                record.setActionTypeName("审批通过");
                record.setStatus("completed");
            } else if ("rejected".equals(flow.getStatus())) {
                record.setActionType("reject");
                record.setActionTypeName("审批拒绝");
                record.setStatus("completed");
            } else if ("returned".equals(flow.getStatus())) {
                record.setActionType("return");
                record.setActionTypeName("审批退回");
                record.setStatus("completed");
            } else if ("pending".equals(flow.getStatus())) {
                record.setActionType("approve");
                record.setActionTypeName("待审批");
                record.setStatus("pending");
            } else {
                record.setActionType("skip");
                record.setActionTypeName("已跳过");
                record.setStatus("completed");
            }

            timeline.add(record);
        }

        return timeline;
    }

    @Override
    public Boolean checkApprovalPermission(Long approvalId, Long userId) {
        LambdaQueryWrapper<ContractApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractApprovalFlow::getApprovalId, approvalId)
                .eq(ContractApprovalFlow::getApproverId, userId)
                .eq(ContractApprovalFlow::getStatus, "pending");
        return approvalFlowMapper.selectCount(wrapper) > 0;
    }

    /**
     * 生成审批单号
     */
    private String generateApprovalNo() {
        String prefix = "AP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<ContractApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(ContractApproval::getApprovalNo, prefix)
                .orderByDesc(ContractApproval::getApprovalNo)
                .last("LIMIT 1");
        ContractApproval lastApproval = getOne(wrapper);

        int seq = 1;
        if (lastApproval != null && StrUtil.isNotBlank(lastApproval.getApprovalNo())) {
            String lastNo = lastApproval.getApprovalNo();
            if (lastNo.length() > prefix.length()) {
                seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }

    /**
     * 根据角色获取审批人ID
     * 实际应该从用户角色表查询
     */
    private Long getApproverByRole(String roleCode) {
        // 简化处理，返回固定值
        // 实际应该从用户角色关联表查询具有该角色的用户
        return 1L;
    }

    /**
     * 跳过剩余的审批流程
     */
    private void skipRemainingFlows(Long approvalId) {
        LambdaQueryWrapper<ContractApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractApprovalFlow::getApprovalId, approvalId)
                .in(ContractApprovalFlow::getStatus, "pending", "waiting");
        List<ContractApprovalFlow> flows = approvalFlowMapper.selectList(wrapper);
        for (ContractApprovalFlow flow : flows) {
            flow.setStatus("skipped");
            approvalFlowMapper.updateById(flow);
        }
    }

    /**
     * 发布审批事件
     */
    private void publishApprovalEvent(Long approvalId, Long contractId, String eventType,
                                       Long approverId, String approverName,
                                       Long submitterId, String submitterName,
                                       String contractNo, String remark) {
        ContractApprovalEvent event = new ContractApprovalEvent(
                this, approvalId, contractId, eventType,
                approverId, approverName, submitterId, submitterName,
                contractNo, remark
        );
        eventPublisher.publishEvent(event);
    }
}
