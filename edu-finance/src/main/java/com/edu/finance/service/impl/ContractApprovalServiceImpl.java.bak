package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ContractApprovalProcessDTO;
import com.edu.finance.domain.dto.ContractApprovalSubmitDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.ContractApproval;
import com.edu.finance.domain.entity.ContractApprovalFlow;
import com.edu.finance.mapper.ContractApprovalFlowMapper;
import com.edu.finance.mapper.ContractApprovalMapper;
import com.edu.finance.mapper.ContractMapper;
import com.edu.finance.service.ContractApprovalService;
import com.edu.framework.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 合同审批服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractApprovalServiceImpl extends ServiceImpl<ContractApprovalMapper, ContractApproval>
        implements ContractApprovalService {

    private final ContractApprovalMapper approvalMapper;
    private final ContractApprovalFlowMapper approvalFlowMapper;
    private final ContractMapper contractMapper;

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
        approval.setTotalSteps(1); // 简化为单级审批，可扩展为多级

        approvalMapper.insert(approval);

        // 创建审批流程（简化为单级审批，审批人为管理员或财务主管）
        // 实际应用中可以根据合同金额、类型等配置不同的审批流程
        ContractApprovalFlow flow = new ContractApprovalFlow();
        flow.setApprovalId(approval.getId());
        flow.setStepNo(1);
        flow.setApproverId(getDefaultApproverId()); // 获取默认审批人
        flow.setApproverName("财务主管"); // 实际应从用户表查询
        flow.setStatus("pending");

        approvalFlowMapper.insert(flow);

        log.info("合同审批提交成功: approvalId={}, contractId={}", approval.getId(), submitDTO.getContractId());
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

        // 查询当前用户的审批流程
        LambdaQueryWrapper<ContractApprovalFlow> flowWrapper = new LambdaQueryWrapper<>();
        flowWrapper.eq(ContractApprovalFlow::getApprovalId, processDTO.getApprovalId())
                .eq(ContractApprovalFlow::getApproverId, currentUserId)
                .eq(ContractApprovalFlow::getStatus, "pending");
        ContractApprovalFlow flow = approvalFlowMapper.selectOne(flowWrapper);

        if (flow == null) {
            throw new BusinessException("您没有权限审批该合同");
        }

        // 更新审批流程
        flow.setStatus(processDTO.getResult());
        flow.setApproveTime(LocalDateTime.now());
        flow.setApproveRemark(processDTO.getApproveRemark());
        approvalFlowMapper.updateById(flow);

        // 更新审批记录
        approval.setStatus(processDTO.getResult());
        approval.setApproverId(currentUserId);
        approval.setApproveTime(LocalDateTime.now());
        approval.setApproveRemark(processDTO.getApproveRemark());
        approvalMapper.updateById(approval);

        // 如果审批通过，更新合同状态
        if ("approved".equals(processDTO.getResult())) {
            Contract contract = contractMapper.selectById(approval.getContractId());
            if (contract != null && "pending".equals(contract.getStatus())) {
                contract.setStatus("signed");
                contractMapper.updateById(contract);
            }
        }

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
        LambdaQueryWrapper<ContractApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractApprovalFlow::getApprovalId, approvalId)
                .eq(ContractApprovalFlow::getStatus, "pending");
        List<ContractApprovalFlow> flows = approvalFlowMapper.selectList(wrapper);
        for (ContractApprovalFlow flow : flows) {
            flow.setStatus("skipped");
            approvalFlowMapper.updateById(flow);
        }

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
     * 获取默认审批人ID
     * 实际应用中应该根据配置或规则获取
     */
    private Long getDefaultApproverId() {
        // 简化处理，返回固定值
        // 实际应该从配置表或用户角色中获取财务主管/管理员
        return 1L;
    }
}
