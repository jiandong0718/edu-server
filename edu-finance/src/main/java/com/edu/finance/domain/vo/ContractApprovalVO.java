package com.edu.finance.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同审批VO
 */
@Data
public class ContractApprovalVO {

    /**
     * 审批ID
     */
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 学员姓名
     */
    private String studentName;

    /**
     * 合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 审批单号
     */
    private String approvalNo;

    /**
     * 审批类型：contract-合同审批，change-变更审批，cancel-作废审批
     */
    private String approvalType;

    /**
     * 审批类型名称
     */
    private String approvalTypeName;

    /**
     * 审批状态：pending-待审批，approved-已通过，rejected-已拒绝，cancelled-已撤销
     */
    private String status;

    /**
     * 审批状态名称
     */
    private String statusName;

    /**
     * 提交人ID
     */
    private Long submitterId;

    /**
     * 提交人姓名
     */
    private String submitterName;

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
     * 审批人姓名
     */
    private String approverName;

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
     * 审批流程列表
     */
    private List<ContractApprovalFlowVO> flowList;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
