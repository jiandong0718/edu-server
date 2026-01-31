package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 课时批量调整DTO
 */
@Data
@Schema(description = "课时批量调整DTO")
public class ClassHourBatchAdjustDTO {

    /**
     * 调整列表
     */
    @NotEmpty(message = "调整列表不能为空")
    @Valid
    @Schema(description = "调整列表", required = true)
    private List<ClassHourAdjustDTO> adjustments;

    /**
     * 是否需要审批
     */
    @Schema(description = "是否需要审批", example = "false")
    private Boolean needApproval;

    /**
     * 审批人ID（需要审批时必填）
     */
    @Schema(description = "审批人ID（需要审批时必填）", example = "1")
    private Long approverId;
}
