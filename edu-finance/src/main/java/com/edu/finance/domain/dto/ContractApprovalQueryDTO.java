package com.edu.finance.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同审批查询DTO
 */
@Data
public class ContractApprovalQueryDTO {

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 审批单号
     */
    private String approvalNo;

    /**
     * 审批类型：contract-合同审批，change-变更审批，cancel-作废审批
     */
    private String approvalType;

    /**
     * 审批状态：pending-待审批，approved-已通过，rejected-已拒绝，cancelled-已撤销
     */
    private String status;

    /**
     * 提交人ID
     */
    private Long submitterId;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 提交开始时间
     */
    private LocalDateTime submitTimeStart;

    /**
     * 提交结束时间
     */
    private LocalDateTime submitTimeEnd;

    /**
     * 审批开始时间
     */
    private LocalDateTime approveTimeStart;

    /**
     * 审批结束时间
     */
    private LocalDateTime approveTimeEnd;
}
