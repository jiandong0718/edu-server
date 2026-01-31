package com.edu.finance.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * 退费完成事件
 * 用于触发课时账户调整、通知发送等后续操作
 */
@Getter
public class RefundCompletedEvent extends ApplicationEvent {

    /**
     * 退费申请ID
     */
    private final Long refundId;

    /**
     * 合同ID
     */
    private final Long contractId;

    /**
     * 学员ID
     */
    private final Long studentId;

    /**
     * 校区ID
     */
    private final Long campusId;

    /**
     * 退费金额
     */
    private final BigDecimal refundAmount;

    public RefundCompletedEvent(Object source, Long refundId, Long contractId, Long studentId, Long campusId, BigDecimal refundAmount) {
        super(source);
        this.refundId = refundId;
        this.contractId = contractId;
        this.studentId = studentId;
        this.campusId = campusId;
        this.refundAmount = refundAmount;
    }
}
