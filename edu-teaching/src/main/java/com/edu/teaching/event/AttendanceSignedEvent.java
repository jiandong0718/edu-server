package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 考勤签到事件
 * 用于触发课时扣减、通知等后续操作
 */
@Getter
public class AttendanceSignedEvent extends ApplicationEvent {

    /**
     * 考勤记录ID
     */
    private final Long attendanceId;

    /**
     * 排课ID
     */
    private final Long scheduleId;

    /**
     * 学员ID
     */
    private final Long studentId;

    /**
     * 班级ID
     */
    private final Long classId;

    /**
     * 课程ID
     */
    private final Long courseId;

    /**
     * 考勤状态：present-出勤，absent-缺勤，late-迟到，leave-请假
     */
    private final String status;

    /**
     * 签到时间
     */
    private final LocalDateTime signTime;

    /**
     * 消耗课时
     */
    private final Integer classHours;

    /**
     * 校区ID
     */
    private final Long campusId;

    public AttendanceSignedEvent(Object source, Long attendanceId, Long scheduleId,
                                 Long studentId, Long classId, Long courseId,
                                 String status, LocalDateTime signTime,
                                 Integer classHours, Long campusId) {
        super(source);
        this.attendanceId = attendanceId;
        this.scheduleId = scheduleId;
        this.studentId = studentId;
        this.classId = classId;
        this.courseId = courseId;
        this.status = status;
        this.signTime = signTime;
        this.classHours = classHours;
        this.campusId = campusId;
    }
}
