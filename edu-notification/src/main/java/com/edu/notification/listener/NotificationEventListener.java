package com.edu.notification.listener;

import com.edu.notification.event.*;
import com.edu.notification.service.NotificationRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 通知事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRuleEngine notificationRuleEngine;

    /**
     * 监听学员注册事件
     */
    @Async
    @EventListener
    public void handleStudentRegister(StudentRegisterEvent event) {
        log.info("接收到学员注册事件，学员ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }

    /**
     * 监听合同签署事件
     */
    @Async
    @EventListener
    public void handleContractSigned(ContractSignedEvent event) {
        log.info("接收到合同签署事件，合同ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }

    /**
     * 监听支付成功事件
     */
    @Async
    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("接收到支付成功事件，支付ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }

    /**
     * 监听上课提醒事件
     */
    @Async
    @EventListener
    public void handleClassRemind(ClassRemindEvent event) {
        log.info("接收到上课提醒事件，课程ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }

    /**
     * 监听缺勤事件
     */
    @Async
    @EventListener
    public void handleAttendanceAbsent(AttendanceAbsentEvent event) {
        log.info("接收到缺勤事件，考勤ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }

    /**
     * 监听课时不足事件
     */
    @Async
    @EventListener
    public void handleClassHourLow(ClassHourLowEvent event) {
        log.info("接收到课时不足事件，账户ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }

    /**
     * 监听试听预约事件
     */
    @Async
    @EventListener
    public void handleTrialLesson(TrialLessonEvent event) {
        log.info("接收到试听预约事件，试听ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }

    /**
     * 监听合同到期事件
     */
    @Async
    @EventListener
    public void handleContractExpire(ContractExpireEvent event) {
        log.info("接收到合同到期事件，合同ID：{}", event.getBizId());
        notificationRuleEngine.processEvent(event);
    }
}
