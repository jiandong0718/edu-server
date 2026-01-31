package com.edu.marketing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 试听预约请求DTO
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@Schema(description = "试听预约请求")
public class TrialAppointmentDTO {

    @Schema(description = "线索ID")
    private Long leadId;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "课程ID", required = true)
    private Long courseId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "校区ID", required = true)
    private Long campusId;

    @Schema(description = "试听日期", required = true)
    private LocalDate trialDate;

    @Schema(description = "试听时间", required = true)
    private LocalTime trialTime;

    @Schema(description = "顾问ID")
    private Long advisorId;

    @Schema(description = "备注")
    private String remark;
}
