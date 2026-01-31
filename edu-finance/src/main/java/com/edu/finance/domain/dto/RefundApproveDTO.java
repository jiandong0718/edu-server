package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 退费审批DTO
 */
@Data
@Schema(description = "退费审批DTO")
public class RefundApproveDTO {

    /**
     * 退费申请ID
     */
    @NotNull(message = "退费申请ID不能为空")
    @Schema(description = "退费申请ID", required = true)
    private Long refundId;

    /**
     * 审批结果：approved-通过，rejected-拒绝
     */
    @NotBlank(message = "审批结果不能为空")
    @Schema(description = "审批结果：approved-通过，rejected-拒绝", required = true)
    private String approveResult;

    /**
     * 实际退费金额（审批通过时必填）
     */
    @Schema(description = "实际退费金额（审批通过时必填）")
    private BigDecimal actualAmount;

    /**
     * 审批备注
     */
    @Schema(description = "审批备注", example = "同意退费申请")
    private String approveRemark;
}
