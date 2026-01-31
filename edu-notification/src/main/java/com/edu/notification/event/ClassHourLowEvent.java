package com.edu.notification.event;

import java.util.Map;

/**
 * 课时不足事件
 */
public class ClassHourLowEvent extends BusinessEvent {

    public ClassHourLowEvent(Object source, Map<String, Object> eventData, Long campusId, Long accountId) {
        super(source, "CLASS_HOUR_LOW", eventData, campusId, accountId);
    }
}
