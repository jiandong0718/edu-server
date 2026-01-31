package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 代课事件
 * 用于通知相关人员代课信息
 */
@Getter
public class SubstituteTeacherEvent extends ApplicationEvent {

    /**
     * 排课ID
     */
    private final Long scheduleId;

    /**
     * 班级ID
     */
    private final Long classId;

    /**
     * 课程ID
     */
    private final Long courseId;

    /**
     * 原教师ID
     */
    private final Long originalTeacherId;

    /**
     * 原教师姓名
     */
    private final String originalTeacherName;

    /**
     * 代课教师ID
     */
    private final Long substituteTeacherId;

    /**
     * 代课教师姓名
     */
    private final String substituteTeacherName;

    /**
     * 上课日期
     */
    private final LocalDate scheduleDate;

    /**
     * 开始时间
     */
    private final LocalTime startTime;

    /**
     * 结束时间
     */
    private final LocalTime endTime;

    /**
     * 代课原因
     */
    private final String reason;

    /**
     * 校区ID
     */
    private final Long campusId;

    public SubstituteTeacherEvent(Object source, Long scheduleId, Long classId, Long courseId,
                                  Long originalTeacherId, String originalTeacherName,
                                  Long substituteTeacherId, String substituteTeacherName,
                                  LocalDate scheduleDate, LocalTime startTime, LocalTime endTime,
                                  String reason, Long campusId) {
        super(source);
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.courseId = courseId;
        this.originalTeacherId = originalTeacherId;
        this.originalTeacherName = originalTeacherName;
        this.substituteTeacherId = substituteTeacherId;
        this.substituteTeacherName = substituteTeacherName;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.campusId = campusId;
    }
}
