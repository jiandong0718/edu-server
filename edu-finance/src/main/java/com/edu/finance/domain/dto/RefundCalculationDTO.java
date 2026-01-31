package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退费金额计算结果DTO
 */
@Data
@Schema(description = "退费金额计算结果DTO")
public class RefundCalculationDTO {

    /**
     * 合同总金额
     */
    @Schema(description = "合同总金额")
    private BigDecimal contractAmount;

    /**
     * 已支付金额
     */
    @Schema(description = "已支付金额")
    private BigDecimal paidAmount;

    /**
     * 总课时数
     */
    @Schema(description = "总课时数")
    private BigDecimal totalHours;

    /**
     * 已消耗课时数
     */
    @Schema(description = "已消耗课时数")
    private BigDecimal usedHours;

    /**
     * 剩余课时数
     */
    @Schema(description = "剩余课时数")
    private BigDecimal remainingHours;

    /**
     * 单课时价格
     */
    @Schema(description = "单课时价格")
    private BigDecimal pricePerHour;

    /**
     * 已消耗金额
     */
    @Schema(description = "已消耗金额")
    private BigDecimal usedAmount;

    /**
     * 违约金
     */
    @Schema(description = "违约金")
    private BigDecimal penaltyAmount;

    /**
     * 违约金比例
     */
    @Schema(description = "违约金比例（百分比）")
    private BigDecimal penaltyRate;

    /**
     * 可退金额
     */
    @Schema(description = "可退金额")
    private BigDecimal refundableAmount;

    /**
     * 计算说明
     */
    @Schema(description = "计算说明")
    private String calculationNote;
}
