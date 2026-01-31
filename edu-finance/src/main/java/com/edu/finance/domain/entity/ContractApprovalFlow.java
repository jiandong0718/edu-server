package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 合同审批流程实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_contract_approval_flow")
public class ContractApprovalFlow extends BaseEntity {

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
     * 状态：pending-待审批，approved-已通过，rejected-已拒绝，skipped-已跳过
     */
    private String status;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approveRemark;
}
