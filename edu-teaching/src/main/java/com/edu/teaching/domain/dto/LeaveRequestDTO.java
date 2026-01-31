package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 请假申请DTO
 */
@Data
@Schema(description = "请假申请DTO")
public class LeaveRequestDTO {

    @Schema(description = "请假申请ID（修改时必填）")
    private Long id;

    @NotNull(message = "学员ID不能为空")
    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "排课ID（单次请假时必填）")
    private Long scheduleId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "校区ID")
    private Long campusId;

    @NotNull(message = "请假类型不能为空")
    @Schema(description = "请假类型：single-单次请假，period-时段请假")
    private String type;

    @Schema(description = "开始日期（时段请假时必填）")
    private LocalDate startDate;

    @Schema(description = "结束日期（时段请假时必填）")
    private LocalDate endDate;

    @NotNull(message = "请假原因不能为空")
    @Schema(description = "请假原因")
    private String reason;

    @Schema(description = "是否需要补课：0-不需要，1-需要", defaultValue = "0")
    private Integer needMakeup;

    @Schema(description = "备注")
    private String remark;
}
