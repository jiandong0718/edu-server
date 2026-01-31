package com.edu.finance.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * 课时预警事件
 * 用于通知相关人员（学员、家长、顾问）
 */
@Getter
public class ClassHourWarningEvent extends ApplicationEvent {

    /**
     * 预警类型：low_balance-余额不足，expiring-即将过期
     */
    private final String warningType;

    /**
     * 账户ID
     */
    private final Long accountId;

    /**
     * 学员ID
     */
    private final Long studentId;

    /**
     * 课程ID
     */
    private final Long courseId;

    /**
     * 剩余课时
     */
    private final BigDecimal remainingHours;

    /**
     * 预警阈值
     */
    private final BigDecimal threshold;

    /**
     * 预警消息
     */
    private final String message;

    /**
     * 是否发送通知
     */
    private final Boolean sendNotification;

    public ClassHourWarningEvent(Object source, String warningType, Long accountId,
                                 Long studentId, Long courseId, BigDecimal remainingHours,
                                 BigDecimal threshold, String message, Boolean sendNotification) {
        super(source);
        this.warningType = warningType;
        this.accountId = accountId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.remainingHours = remainingHours;
        this.threshold = threshold;
        this.message = message;
        this.sendNotification = sendNotification;
    }
}
