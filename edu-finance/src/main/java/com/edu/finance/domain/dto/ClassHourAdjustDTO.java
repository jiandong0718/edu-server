package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 课时调整DTO
 */
@Data
@Schema(description = "课时调整DTO")
public class ClassHourAdjustDTO {

    /**
     * 账户ID
     */
    @NotNull(message = "账户ID不能为空")
    @Schema(description = "账户ID", required = true)
    private Long accountId;

    /**
     * 调整类型：gift-赠送，deduct-扣减，revoke-撤销
     */
    @NotNull(message = "调整类型不能为空")
    @Schema(description = "调整类型：gift-赠送，deduct-扣减，revoke-撤销", required = true)
    private String adjustType;

    /**
     * 调整课时数（正数）
     */
    @NotNull(message = "调整课时数不能为空")
    @Schema(description = "调整课时数（正数）", required = true)
    private BigDecimal hours;

    /**
     * 原记录ID（撤销时需要）
     */
    @Schema(description = "原记录ID（撤销时需要）")
    private Long originalRecordId;

    /**
     * 调整原因
     */
    @NotNull(message = "调整原因不能为空")
    @Schema(description = "调整原因", required = true)
    private String reason;
}
