package com.edu.notification.event;

import java.util.Map;

/**
 * 上课提醒事件
 */
public class ClassRemindEvent extends BusinessEvent {

    public ClassRemindEvent(Object source, Map<String, Object> eventData, Long campusId, Long classId) {
        super(source, "CLASS_REMIND", eventData, campusId, classId);
    }
}
