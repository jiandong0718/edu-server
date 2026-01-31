package com.edu.teaching.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 请假审批DTO
 */
@Data
@Schema(description = "请假审批DTO")
public class LeaveApprovalDTO {

    @NotNull(message = "请假申请ID不能为空")
    @Schema(description = "请假申请ID")
    private Long id;

    @NotNull(message = "审批结果不能为空")
    @Schema(description = "是否批准：true-批准，false-拒绝")
    private Boolean approved;

    @Schema(description = "审批意见")
    private String remark;
}
