package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 停课请求DTO
 */
@Data
@Schema(description = "停课请求DTO")
public class CancelScheduleDTO {

    @NotNull(message = "排课ID不能为空")
    @Schema(description = "排课ID")
    private Long scheduleId;

    @NotNull(message = "停课原因不能为空")
    @Schema(description = "停课原因")
    private String cancelReason;

    @Schema(description = "是否需要补课", defaultValue = "false")
    private Boolean needMakeup;

    @Schema(description = "补课日期（格式：yyyy-MM-dd）")
    private String makeupDate;

    @Schema(description = "补课开始时间（格式：HH:mm）")
    private String makeupStartTime;

    @Schema(description = "补课结束时间（格式：HH:mm）")
    private String makeupEndTime;

    @Schema(description = "备注")
    private String remark;
}
