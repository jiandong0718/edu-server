package com.edu.notification.event;

import java.util.Map;

/**
 * 支付成功事件
 */
public class PaymentSuccessEvent extends BusinessEvent {

    public PaymentSuccessEvent(Object source, Map<String, Object> eventData, Long campusId, Long paymentId) {
        super(source, "PAYMENT_SUCCESS", eventData, campusId, paymentId);
    }
}
