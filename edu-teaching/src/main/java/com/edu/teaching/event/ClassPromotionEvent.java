package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 班级升班事件
 */
@Getter
public class ClassPromotionEvent extends ApplicationEvent {

    /**
     * 原班级ID
     */
    private final Long originalClassId;

    /**
     * 原班级名称
     */
    private final String originalClassName;

    /**
     * 目标班级ID
     */
    private final Long targetClassId;

    /**
     * 目标班级名称
     */
    private final String targetClassName;

    /**
     * 转移的学员ID列表
     */
    private final List<Long> transferredStudentIds;

    /**
     * 是否创建了新班级
     */
    private final Boolean newClassCreated;

    /**
     * 备注
     */
    private final String remark;

    public ClassPromotionEvent(Object source, Long originalClassId, String originalClassName,
                               Long targetClassId, String targetClassName,
                               List<Long> transferredStudentIds, Boolean newClassCreated, String remark) {
        super(source);
        this.originalClassId = originalClassId;
        this.originalClassName = originalClassName;
        this.targetClassId = targetClassId;
        this.targetClassName = targetClassName;
        this.transferredStudentIds = transferredStudentIds;
        this.newClassCreated = newClassCreated;
        this.remark = remark;
    }
}
