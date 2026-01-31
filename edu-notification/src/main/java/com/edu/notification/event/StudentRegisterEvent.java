package com.edu.notification.event;

import java.util.Map;

/**
 * 学员注册事件
 */
public class StudentRegisterEvent extends BusinessEvent {

    public StudentRegisterEvent(Object source, Map<String, Object> eventData, Long campusId, Long studentId) {
        super(source, "STUDENT_REGISTER", eventData, campusId, studentId);
    }
}
