package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 补课安排DTO
 */
@Data
@Schema(description = "补课安排DTO")
public class MakeupLessonDTO {

    @Schema(description = "补课记录ID（修改时必填）")
    private Long id;

    @Schema(description = "请假申请ID")
    private Long leaveRequestId;

    @NotNull(message = "原排课ID不能为空")
    @Schema(description = "原排课ID")
    private Long originalScheduleId;

    @NotNull(message = "补课排课ID不能为空")
    @Schema(description = "补课排课ID")
    private Long makeupScheduleId;

    @NotNull(message = "学员ID不能为空")
    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "备注")
    private String remark;
}
