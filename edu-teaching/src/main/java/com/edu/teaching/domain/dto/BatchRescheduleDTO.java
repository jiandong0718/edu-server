package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 批量调课请求DTO
 */
@Data
@Schema(description = "批量调课请求DTO")
public class BatchRescheduleDTO {

    @NotEmpty(message = "排课ID列表不能为空")
    @Schema(description = "排课ID列表")
    private List<Long> scheduleIds;

    @Schema(description = "新上课日期（不修改则不传）")
    private LocalDate newScheduleDate;

    @Schema(description = "新开始时间（不修改则不传）")
    private LocalTime newStartTime;

    @Schema(description = "新结束时间（不修改则不传）")
    private LocalTime newEndTime;

    @Schema(description = "新教师ID（不修改则不传）")
    private Long newTeacherId;

    @Schema(description = "新教室ID（不修改则不传）")
    private Long newClassroomId;

    @NotNull(message = "调课原因不能为空")
    @Schema(description = "调课原因")
    private String reason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否发送通知", defaultValue = "true")
    private Boolean sendNotification = true;
}
