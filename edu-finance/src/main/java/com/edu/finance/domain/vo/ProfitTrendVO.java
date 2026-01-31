package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 利润趋势分析VO
 */
@Data
@Schema(description = "利润趋势分析VO")
public class ProfitTrendVO {

    /**
     * 日期（格式：YYYY-MM 或 YYYY-MM-DD）
     */
    @Schema(description = "日期")
    private String date;

    /**
     * 收入
     */
    @Schema(description = "收入")
    private BigDecimal revenue;

    /**
     * 成本
     */
    @Schema(description = "成本")
    private BigDecimal cost;

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
}
