package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量保存教师可用时间DTO
 */
@Data
@Schema(description = "批量保存教师可用时间DTO")
public class BatchSaveAvailableTimeDTO {

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID")
    private Long teacherId;

    @NotEmpty(message = "可用时间列表不能为空")
    @Valid
    @Schema(description = "可用时间列表")
    private List<TimeSlotDTO> timeSlots;

    /**
     * 时间段DTO
     */
    @Data
    @Schema(description = "时间段DTO")
    public static class TimeSlotDTO {

        @NotNull(message = "星期几不能为空")
        @Schema(description = "星期几：1-7（1表示周一，7表示周日）")
        private Integer dayOfWeek;

        @NotEmpty(message = "开始时间不能为空")
        @Schema(description = "开始时间（HH:mm格式，如：09:00）")
        private String startTime;

        @NotEmpty(message = "结束时间不能为空")
        @Schema(description = "结束时间（HH:mm格式，如：17:00）")
        private String endTime;

        @Schema(description = "备注")
        private String remark;
    }
}
