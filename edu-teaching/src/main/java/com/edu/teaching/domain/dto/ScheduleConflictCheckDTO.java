package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排课冲突检测请求DTO
 */
@Data
@Schema(description = "排课冲突检测请求")
public class ScheduleConflictCheckDTO {

    @Schema(description = "排课ID（编辑时传入，用于排除自身）")
    private Long scheduleId;

    @NotNull(message = "上课日期不能为空")
    @Schema(description = "上课日期", required = true)
    private LocalDate scheduleDate;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", required = true)
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", required = true)
    private LocalTime endTime;

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID", required = true)
    private Long teacherId;

    @NotNull(message = "教室ID不能为空")
    @Schema(description = "教室ID", required = true)
    private Long classroomId;

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID", required = true)
    private Long classId;
}
