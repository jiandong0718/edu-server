package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 按校区利润分析VO
 */
@Data
@Schema(description = "按校区利润分析VO")
public class CampusProfitAnalysisVO {

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String campusName;

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

    /**
     * 学员数量
     */
    @Schema(description = "学员数量")
    private Integer studentCount;
}
