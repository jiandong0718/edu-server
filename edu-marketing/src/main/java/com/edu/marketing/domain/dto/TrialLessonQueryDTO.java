package com.edu.marketing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 试听记录查询DTO
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@Schema(description = "试听记录查询DTO")
public class TrialLessonQueryDTO {

    @Schema(description = "线索ID")
    private Long leadId;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "状态：appointed-已预约，attended-已到场，absent-未到场，converted-已转化")
    private String status;

    @Schema(description = "顾问ID")
    private Long advisorId;

    @Schema(description = "试听日期（开始）")
    private LocalDate trialDateStart;

    @Schema(description = "试听日期（结束）")
    private LocalDate trialDateEnd;

    @Schema(description = "线索姓名（模糊查询）")
    private String leadName;

    @Schema(description = "学员姓名（模糊查询）")
    private String studentName;

    @Schema(description = "手机号（模糊查询）")
    private String phone;
}
