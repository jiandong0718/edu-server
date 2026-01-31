package com.edu.finance.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 合同审批提交DTO
 */
@Data
public class ContractApprovalSubmitDTO {

    /**
     * 合同ID
     */
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
     * 审批类型：contract-合同审批，change-变更审批，cancel-作废审批
     */
    @NotNull(message = "审批类型不能为空")
    private String approvalType;

    /**
     * 提交原因
     */
    private String submitReason;
}
