package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 利润分析概览VO
 */
@Data
@Schema(description = "利润分析概览VO")
public class ProfitAnalysisOverviewVO {

    /**
     * 总收入
     */
    @Schema(description = "总收入")
    private BigDecimal totalRevenue;

    /**
     * 总成本
     */
    @Schema(description = "总成本")
    private BigDecimal totalCost;

    /**
     * 毛利润
     */
    @Schema(description = "毛利润")
    private BigDecimal grossProfit;

    /**
     * 毛利率（百分比）
     */
    @Schema(description = "毛利率")
    private BigDecimal grossProfitMargin;

    /**
     * 合同数量
     */
    @Schema(description = "合同数量")
    private Integer contractCount;

    /**
     * 学员数量
     */
    @Schema(description = "学员数量")
    private Integer studentCount;

    /**
     * 平均客单价
     */
    @Schema(description = "平均客单价")
    private BigDecimal avgContractAmount;
}
