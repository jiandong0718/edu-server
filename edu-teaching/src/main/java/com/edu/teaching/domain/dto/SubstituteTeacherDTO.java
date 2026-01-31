package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 代课请求DTO
 */
@Data
@Schema(description = "代课请求DTO")
public class SubstituteTeacherDTO {

    @NotNull(message = "排课ID不能为空")
    @Schema(description = "排课ID")
    private Long scheduleId;

    @NotNull(message = "代课教师ID不能为空")
    @Schema(description = "代课教师ID")
    private Long substituteTeacherId;

    @Schema(description = "代课原因")
    private String reason;

    @Schema(description = "备注")
    private String remark;
}
