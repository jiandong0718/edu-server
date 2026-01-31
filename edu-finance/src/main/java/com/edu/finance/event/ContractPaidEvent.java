package com.edu.finance.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 合同支付完成事件
 * 用于触发课时账户创建等后续操作
 */
@Getter
public class ContractPaidEvent extends ApplicationEvent {

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
     * 支付金额
     */
    private final java.math.BigDecimal paidAmount;

    public ContractPaidEvent(Object source, Long contractId, Long studentId, Long campusId, java.math.BigDecimal paidAmount) {
        super(source);
        this.contractId = contractId;
        this.studentId = studentId;
        this.campusId = campusId;
        this.paidAmount = paidAmount;
    }
}
