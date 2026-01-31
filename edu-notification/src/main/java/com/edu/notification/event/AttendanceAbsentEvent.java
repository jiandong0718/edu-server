package com.edu.notification.event;

import java.util.Map;

/**
 * 缺勤事件
 */
public class AttendanceAbsentEvent extends BusinessEvent {

    public AttendanceAbsentEvent(Object source, Map<String, Object> eventData, Long campusId, Long attendanceId) {
        super(source, "ATTENDANCE_ABSENT", eventData, campusId, attendanceId);
    }
}
