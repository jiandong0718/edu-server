package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_homework")
public class Homework extends BaseEntity {

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业内容
     */
    private String content;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 排课ID（关联到具体课节）
     */
    private Long scheduleId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 作业类型：practice-练习，test-测验，project-项目
     */
    private String type;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 附件URL（JSON数组）
     */
    private String attachments;

    /**
     * 状态：draft-草稿，published-已发布，closed-已截止
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 班级名称
     */
    @TableField(exist = false)
    private String className;

    /**
     * 课程名称
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 教师姓名
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 提交数量
     */
    @TableField(exist = false)
    private Integer submitCount;

    /**
     * 班级学员数量
     */
    @TableField(exist = false)
    private Integer studentCount;
}
