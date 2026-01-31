package com.edu.finance.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 合同审批处理DTO
 */
@Data
public class ContractApprovalProcessDTO {

    /**
     * 审批ID
     */
    @NotNull(message = "审批ID不能为空")
    private Long approvalId;

    /**
     * 审批结果：approved-通过，rejected-拒绝，returned-退回
     */
    @NotNull(message = "审批结果不能为空")
    private String result;

    /**
     * 审批意见
     */
    private String approveRemark;
}
