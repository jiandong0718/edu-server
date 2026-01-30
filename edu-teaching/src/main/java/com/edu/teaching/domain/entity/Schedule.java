package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排课/课节实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_schedule")
public class Schedule extends BaseEntity {

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教室ID
     */
    private Long classroomId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 上课日期
     */
    private LocalDate scheduleDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 课时数
     */
    private Integer classHours;

    /**
     * 状态：scheduled-已排课，ongoing-进行中，finished-已完成，cancelled-已取消
     */
    private String status;

    /**
     * 课节序号
     */
    private Integer lessonNo;

    /**
     * 课节主题
     */
    private String topic;

    /**
     * 备注
     */
    private String remark;

    /**
     * 班级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String className;

    /**
     * 课程名称（非数据库字段）
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 教师姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 教室名称（非数据库字段）
     */
    @TableField(exist = false)
    private String classroomName;
}
