package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 停课事件
 * 用于通知相关人员停课信息
 */
@Getter
public class CancelScheduleEvent extends ApplicationEvent {

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
     * 教师ID
     */
    private final Long teacherId;

    /**
     * 教师姓名
     */
    private final String teacherName;

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
     * 停课原因
     */
    private final String cancelReason;

    /**
     * 是否需要补课
     */
    private final Boolean needMakeup;

    /**
     * 补课日期
     */
    private final LocalDate makeupDate;

    /**
     * 补课开始时间
     */
    private final LocalTime makeupStartTime;

    /**
     * 补课结束时间
     */
    private final LocalTime makeupEndTime;

    /**
     * 校区ID
     */
    private final Long campusId;

    public CancelScheduleEvent(Object source, Long scheduleId, Long classId, Long courseId,
                               Long teacherId, String teacherName,
                               LocalDate scheduleDate, LocalTime startTime, LocalTime endTime,
                               String cancelReason, Boolean needMakeup,
                               LocalDate makeupDate, LocalTime makeupStartTime, LocalTime makeupEndTime,
                               Long campusId) {
        super(source);
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cancelReason = cancelReason;
        this.needMakeup = needMakeup;
        this.makeupDate = makeupDate;
        this.makeupStartTime = makeupStartTime;
        this.makeupEndTime = makeupEndTime;
        this.campusId = campusId;
    }
}
