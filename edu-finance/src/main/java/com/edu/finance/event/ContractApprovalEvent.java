package com.edu.finance.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 合同审批事件
 */
@Getter
public class ContractApprovalEvent extends ApplicationEvent {

    /**
     * 审批ID
     */
    private final Long approvalId;

    /**
     * 合同ID
     */
    private final Long contractId;

    /**
     * 事件类型：submitted-已提交，approved-已通过，rejected-已拒绝，returned-已退回，cancelled-已撤销
     */
    private final String eventType;

    /**
     * 审批人ID
     */
    private final Long approverId;

    /**
     * 审批人姓名
     */
    private final String approverName;

    /**
     * 提交人ID
     */
    private final Long submitterId;

    /**
     * 提交人姓名
     */
    private final String submitterName;

    /**
     * 合同编号
     */
    private final String contractNo;

    /**
     * 审批意见
     */
    private final String remark;

    public ContractApprovalEvent(Object source, Long approvalId, Long contractId, String eventType,
                                  Long approverId, String approverName, Long submitterId,
                                  String submitterName, String contractNo, String remark) {
        super(source);
        this.approvalId = approvalId;
        this.contractId = contractId;
        this.eventType = eventType;
        this.approverId = approverId;
        this.approverName = approverName;
        this.submitterId = submitterId;
        this.submitterName = submitterName;
        this.contractNo = contractNo;
        this.remark = remark;
    }
}
