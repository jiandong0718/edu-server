package com.edu.teaching.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

/**
 * 教师状态变更事件
 * 用于通知相关系统教师状态发生变更
 */
@Getter
public class TeacherStatusChangeEvent extends ApplicationEvent {

    /**
     * 教师ID
     */
    private final Long teacherId;

    /**
     * 教师姓名
     */
    private final String teacherName;

    /**
     * 教师编号
     */
    private final String teacherNo;

    /**
     * 原状态
     */
    private final String fromStatus;

    /**
     * 新状态
     */
    private final String toStatus;

    /**
     * 变更原因
     */
    private final String reason;

    /**
     * 生效日期
     */
    private final LocalDate effectiveDate;

    /**
     * 预计返回日期（休假时有值）
     */
    private final LocalDate expectedReturnDate;

    /**
     * 校区ID
     */
    private final Long campusId;

    /**
     * 操作人ID
     */
    private final Long operatorId;

    /**
     * 操作人姓名
     */
    private final String operatorName;

    public TeacherStatusChangeEvent(Object source, Long teacherId, String teacherName, String teacherNo,
                                    String fromStatus, String toStatus, String reason,
                                    LocalDate effectiveDate, LocalDate expectedReturnDate,
                                    Long campusId, Long operatorId, String operatorName) {
        super(source);
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.teacherNo = teacherNo;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.reason = reason;
        this.effectiveDate = effectiveDate;
        this.expectedReturnDate = expectedReturnDate;
        this.campusId = campusId;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
    }
}
