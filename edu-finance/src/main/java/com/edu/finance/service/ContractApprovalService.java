package com.edu.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.ApprovalRecordDTO;
import com.edu.finance.domain.dto.ContractApprovalProcessDTO;
import com.edu.finance.domain.dto.ContractApprovalQueryDTO;
import com.edu.finance.domain.dto.ContractApprovalSubmitDTO;
import com.edu.finance.domain.entity.ContractApproval;
import com.edu.finance.domain.entity.ContractApprovalFlow;
import com.edu.finance.domain.vo.ContractApprovalVO;

import java.util.List;

/**
 * 合同审批服务接口
 */
public interface ContractApprovalService extends IService<ContractApproval> {

    /**
     * 提交审批
     *
     * @param submitDTO 提交信息
     * @return 审批ID
     */
    Long submitApproval(ContractApprovalSubmitDTO submitDTO);

    /**
     * 处理审批
     *
     * @param processDTO 审批处理信息
     * @return 是否成功
     */
    Boolean processApproval(ContractApprovalProcessDTO processDTO);

    /**
     * 撤销审批
     *
     * @param approvalId 审批ID
     * @return 是否成功
     */
    Boolean cancelApproval(Long approvalId);

    /**
     * 获取审批历史
     *
     * @param contractId 合同ID
     * @return 审批历史列表
     */
    List<ContractApproval> getApprovalHistory(Long contractId);

    /**
     * 获取审批流程
     *
     * @param approvalId 审批ID
     * @return 审批流程列表
     */
    List<ContractApprovalFlow> getApprovalFlow(Long approvalId);

    /**
     * 获取待审批列表
     *
     * @param approverId 审批人ID
     * @return 待审批列表
     */
    List<ContractApproval> getPendingApprovals(Long approverId);

    /**
     * 分页查询待审批列表
     *
     * @param page 分页对象
     * @param approverId 审批人ID
     * @return 分页结果
     */
    IPage<ContractApprovalVO> getPendingApprovalsPage(IPage<ContractApprovalVO> page, Long approverId);

    /**
     * 分页查询审批记录
     *
     * @param page 分页对象
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<ContractApprovalVO> getApprovalPage(IPage<ContractApprovalVO> page, ContractApprovalQueryDTO queryDTO);

    /**
     * 获取审批详情
     *
     * @param approvalId 审批ID
     * @return 审批详情
     */
    ContractApprovalVO getApprovalDetail(Long approvalId);

    /**
     * 获取审批记录时间线
     *
     * @param approvalId 审批ID
     * @return 审批记录列表
     */
    List<ApprovalRecordDTO> getApprovalTimeline(Long approvalId);

    /**
     * 检查用户是否有审批权限
     *
     * @param approvalId 审批ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    Boolean checkApprovalPermission(Long approvalId, Long userId);
}
