package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 教师考勤实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_teacher_attendance")
public class TeacherAttendance extends BaseEntity {

    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 签到时间
     */
    private LocalDateTime signInTime;

    /**
     * 签退时间
     */
    private LocalDateTime signOutTime;

    /**
     * 状态：present-出勤，absent-缺勤，late-迟到，early_leave-早退，leave-请假
     */
    private String status;

    /**
     * 是否迟到
     */
    private Boolean isLate;

    /**
     * 是否早退
     */
    private Boolean isEarlyLeave;

    /**
     * 迟到分钟数
     */
    private Integer lateMinutes;

    /**
     * 早退分钟数
     */
    private Integer earlyLeaveMinutes;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 教师姓名
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 教师编号
     */
    @TableField(exist = false)
    private String teacherNo;

    /**
     * 课程名称
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 班级名称
     */
    @TableField(exist = false)
    private String className;

    /**
     * 上课日期
     */
    @TableField(exist = false)
    private java.time.LocalDate scheduleDate;

    /**
     * 开始时间
     */
    @TableField(exist = false)
    private java.time.LocalTime startTime;

    /**
     * 结束时间
     */
    @TableField(exist = false)
    private java.time.LocalTime endTime;
}
