package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 合同审批记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_contract_approval")
public class ContractApproval extends BaseEntity {

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
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 提交原因
     */
    private String submitReason;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 当前审批步骤
     */
    private Integer currentStep;

    /**
     * 总审批步骤
     */
    private Integer totalSteps;

    /**
     * 提交人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String submitterName;

    /**
     * 审批人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String approverName;

    /**
     * 合同编号（非数据库字段）
     */
    @TableField(exist = false)
    private String contractNo;
}
