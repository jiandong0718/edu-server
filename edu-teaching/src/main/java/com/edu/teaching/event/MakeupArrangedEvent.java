package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 补课安排事件
 */
@Getter
public class MakeupArrangedEvent extends ApplicationEvent {

    /**
     * 补课记录ID
     */
    private final Long makeupLessonId;

    /**
     * 学员ID
     */
    private final Long studentId;

    /**
     * 补课排课ID
     */
    private final Long makeupScheduleId;

    /**
     * 原排课ID
     */
    private final Long originalScheduleId;

    public MakeupArrangedEvent(Object source, Long makeupLessonId, Long studentId,
                               Long makeupScheduleId, Long originalScheduleId) {
        super(source);
        this.makeupLessonId = makeupLessonId;
        this.studentId = studentId;
        this.makeupScheduleId = makeupScheduleId;
        this.originalScheduleId = originalScheduleId;
    }
}
