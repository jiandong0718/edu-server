package com.edu.finance.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * 欠费催缴提醒事件
 */
@Getter
@Setter
public class ArrearsRemindEvent extends ApplicationEvent {

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 欠费金额
     */
    private BigDecimal arrearsAmount;

    /**
     * 提醒方式：sms-短信，message-站内信，both-两者都发
     */
    private String reminderType;

    /**
     * 自定义提醒内容
     */
    private String customMessage;

    public ArrearsRemindEvent(Object source) {
        super(source);
    }
}
