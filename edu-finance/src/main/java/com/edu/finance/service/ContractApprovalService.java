package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.dto.ContractApprovalProcessDTO;
import com.edu.finance.domain.dto.ContractApprovalSubmitDTO;
import com.edu.finance.domain.entity.ContractApproval;
import com.edu.finance.domain.entity.ContractApprovalFlow;

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
}
