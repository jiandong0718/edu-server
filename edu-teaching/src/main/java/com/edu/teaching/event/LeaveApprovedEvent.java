package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 请假审批事件
 */
@Getter
public class LeaveApprovedEvent extends ApplicationEvent {

    /**
     * 请假申请ID
     */
    private final Long leaveRequestId;

    /**
     * 学员ID
     */
    private final Long studentId;

    /**
     * 是否批准
     */
    private final Boolean approved;

    /**
     * 审批意见
     */
    private final String remark;

    public LeaveApprovedEvent(Object source, Long leaveRequestId, Long studentId, Boolean approved, String remark) {
        super(source);
        this.leaveRequestId = leaveRequestId;
        this.studentId = studentId;
        this.approved = approved;
        this.remark = remark;
    }
}
