package com.edu.finance.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同审批流程VO
 */
@Data
public class ContractApprovalFlowVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 审批ID
     */
    private Long approvalId;

    /**
     * 步骤序号
     */
    private Integer stepNo;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批人头像
     */
    private String approverAvatar;

    /**
     * 状态：pending-待审批，approved-已通过，rejected-已拒绝，skipped-已跳过，returned-已退回
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
