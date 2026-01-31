package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 教师签到DTO
 */
@Data
@Schema(description = "教师签到DTO")
public class TeacherAttendanceSignInDTO {

    /**
     * 排课ID
     */
    @NotNull(message = "排课ID不能为空")
    @Schema(description = "排课ID", required = true)
    private Long scheduleId;

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID", required = true)
    private Long teacherId;

    /**
     * 签到时间（可选，不传则使用当前时间）
     */
    @Schema(description = "签到时间（可选，不传则使用当前时间）")
    private LocalDateTime signInTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
