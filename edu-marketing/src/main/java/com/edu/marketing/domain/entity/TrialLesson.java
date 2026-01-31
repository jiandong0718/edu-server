package com.edu.marketing.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 试听记录实体
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mkt_trial_lesson")
public class TrialLesson extends BaseEntity {

    /**
     * 线索ID
     */
    private Long leadId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 试听日期
     */
    private LocalDate trialDate;

    /**
     * 试听时间
     */
    private LocalTime trialTime;

    /**
     * 状态：appointed-已预约，attended-已到场，absent-未到场，converted-已转化
     */
    private String status;

    /**
     * 试听反馈
     */
    private String feedback;

    /**
     * 评分（1-5）
     */
    private Integer rating;

    /**
     * 顾问ID
     */
    private Long advisorId;

    /**
     * 备注
     */
    private String remark;
}
