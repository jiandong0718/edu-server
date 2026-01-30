package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业提交实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_homework_submit")
public class HomeworkSubmit extends BaseEntity {

    /**
     * 作业ID
     */
    private Long homeworkId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 提交内容
     */
    private String content;

    /**
     * 附件URL（JSON数组）
     */
    private String attachments;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 状态：pending-待批改，reviewed-已批改，returned-已退回
     */
    private String status;

    /**
     * 评分（0-100）
     */
    private Integer score;

    /**
     * 评级：A/B/C/D/E
     */
    private String grade;

    /**
     * 教师点评
     */
    private String comment;

    /**
     * 批改人ID
     */
    private Long reviewerId;

    /**
     * 批改时间
     */
    private LocalDateTime reviewTime;

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
     * 作业标题
     */
    @TableField(exist = false)
    private String homeworkTitle;

    /**
     * 批改人姓名
     */
    @TableField(exist = false)
    private String reviewerName;
}
