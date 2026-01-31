package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 补课记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_makeup_lesson")
public class MakeupLesson extends BaseEntity {

    /**
     * 请假申请ID
     */
    private Long leaveRequestId;

    /**
     * 原排课ID
     */
    private Long originalScheduleId;

    /**
     * 补课排课ID
     */
    private Long makeupScheduleId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 状态：pending-待补课，completed-已完成，cancelled-已取消
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 学员姓名
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 学员编号
     */
    @TableField(exist = false)
    private String studentNo;

    /**
     * 原课程日期
     */
    @TableField(exist = false)
    private String originalScheduleDate;

    /**
     * 补课日期
     */
    @TableField(exist = false)
    private String makeupScheduleDate;

    /**
     * 原课程教师
     */
    @TableField(exist = false)
    private String originalTeacherName;

    /**
     * 补课教师
     */
    @TableField(exist = false)
    private String makeupTeacherName;
}
