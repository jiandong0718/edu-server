package com.edu.marketing.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 试听记录VO
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@Schema(description = "试听记录VO")
public class TrialLessonVO {

    @Schema(description = "试听记录ID")
    private Long id;

    @Schema(description = "线索ID")
    private Long leadId;

    @Schema(description = "线索姓名")
    private String leadName;

    @Schema(description = "线索手机号")
    private String leadPhone;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "学员姓名")
    private String studentName;

    @Schema(description = "学员手机号")
    private String studentPhone;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "校区名称")
    private String campusName;

    @Schema(description = "试听日期")
    private LocalDate trialDate;

    @Schema(description = "试听时间")
    private LocalTime trialTime;

    @Schema(description = "状态：appointed-已预约，attended-已到场，absent-未到场，converted-已转化")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "试听反馈")
    private String feedback;

    @Schema(description = "评分（1-5）")
    private Integer rating;

    @Schema(description = "顾问ID")
    private Long advisorId;

    @Schema(description = "顾问姓名")
    private String advisorName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
