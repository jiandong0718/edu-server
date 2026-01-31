package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考勤实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_attendance")
public class Attendance extends BaseEntity {

    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 状态：present-出勤，absent-缺勤，late-迟到，leave-请假
     */
    private String status;

    /**
     * 签到时间
     */
    private LocalDateTime signTime;

    /**
     * 消耗课时
     */
    private Integer classHours;

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
     * 教师姓名
     */
    @TableField(exist = false)
    private String teacherName;
}
