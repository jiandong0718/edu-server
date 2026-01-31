package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 调课事件
 * 用于通知相关人员（教师、学员）
 */
@Getter
public class ScheduleRescheduleEvent extends ApplicationEvent {

    /**
     * 排课ID
     */
    private final Long scheduleId;

    /**
     * 班级ID
     */
    private final Long classId;

    /**
     * 原教师ID
     */
    private final Long oldTeacherId;

    /**
     * 新教师ID
     */
    private final Long newTeacherId;

    /**
     * 原上课日期
     */
    private final LocalDate oldScheduleDate;

    /**
     * 新上课日期
     */
    private final LocalDate newScheduleDate;

    /**
     * 原开始时间
     */
    private final LocalTime oldStartTime;

    /**
     * 新开始时间
     */
    private final LocalTime newStartTime;

    /**
     * 原结束时间
     */
    private final LocalTime oldEndTime;

    /**
     * 新结束时间
     */
    private final LocalTime newEndTime;

    /**
     * 原教室ID
     */
    private final Long oldClassroomId;

    /**
     * 新教室ID
     */
    private final Long newClassroomId;

    /**
     * 调课原因
     */
    private final String reason;

    /**
     * 是否发送通知
     */
    private final Boolean sendNotification;

    public ScheduleRescheduleEvent(Object source, Long scheduleId, Long classId,
                                   Long oldTeacherId, Long newTeacherId,
                                   LocalDate oldScheduleDate, LocalDate newScheduleDate,
                                   LocalTime oldStartTime, LocalTime newStartTime,
                                   LocalTime oldEndTime, LocalTime newEndTime,
                                   Long oldClassroomId, Long newClassroomId,
                                   String reason, Boolean sendNotification) {
        super(source);
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.oldTeacherId = oldTeacherId;
        this.newTeacherId = newTeacherId;
        this.oldScheduleDate = oldScheduleDate;
        this.newScheduleDate = newScheduleDate;
        this.oldStartTime = oldStartTime;
        this.newStartTime = newStartTime;
        this.oldEndTime = oldEndTime;
        this.newEndTime = newEndTime;
        this.oldClassroomId = oldClassroomId;
        this.newClassroomId = newClassroomId;
        this.reason = reason;
        this.sendNotification = sendNotification;
    }
}
