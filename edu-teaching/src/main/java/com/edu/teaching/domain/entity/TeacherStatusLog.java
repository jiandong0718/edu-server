package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师状态变更记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_teacher_status_log")
public class TeacherStatusLog extends BaseEntity {

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 教师编号
     */
    private String teacherNo;

    /**
     * 原状态：active-在职，on_leave-休假，resigned-离职
     */
    private String fromStatus;

    /**
     * 新状态：active-在职，on_leave-休假，resigned-离职
     */
    private String toStatus;

    /**
     * 变更原因
     */
    private String reason;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 预计返回日期（休假时填写）
     */
    private LocalDate expectedReturnDate;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 备注
     */
    private String remark;
}
