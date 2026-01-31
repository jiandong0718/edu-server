package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 教师可用时间配置DTO
 */
@Data
@Schema(description = "教师可用时间配置DTO")
public class TeacherAvailableTimeDTO {

    @Schema(description = "可用时间ID（修改时必填）")
    private Long id;

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID")
    private Long teacherId;

    @NotNull(message = "星期几不能为空")
    @Min(value = 1, message = "星期几必须在1-7之间")
    @Max(value = 7, message = "星期几必须在1-7之间")
    @Schema(description = "星期几：1-7（1表示周一，7表示周日）")
    private Integer dayOfWeek;

    @NotBlank(message = "开始时间不能为空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "开始时间格式必须为HH:mm")
    @Schema(description = "开始时间（HH:mm格式，如：09:00）")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "结束时间格式必须为HH:mm")
    @Schema(description = "结束时间（HH:mm格式，如：17:00）")
    private String endTime;

    @Schema(description = "状态：0-禁用，1-启用", defaultValue = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
