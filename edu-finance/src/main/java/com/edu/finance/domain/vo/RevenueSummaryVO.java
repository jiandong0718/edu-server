package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收入汇总VO
 */
@Data
@Schema(description = "收入汇总VO")
public class RevenueSummaryVO {

    /**
     * 总收入
     */
    @Schema(description = "总收入")
    private BigDecimal totalRevenue;

    /**
     * 总合同数
     */
    @Schema(description = "总合同数")
    private Integer totalContracts;

    /**
     * 总学员数
     */
    @Schema(description = "总学员数")
    private Integer totalStudents;

    /**
     * 平均客单价
     */
    @Schema(description = "平均客单价")
    private BigDecimal avgOrderAmount;

    /**
     * 现金收入
     */
    @Schema(description = "现金收入")
    private BigDecimal cashRevenue;

    /**
     * 转账收入
     */
    @Schema(description = "转账收入")
    private BigDecimal transferRevenue;

    /**
     * 在线支付收入
     */
    @Schema(description = "在线支付收入")
    private BigDecimal onlineRevenue;

    /**
     * 环比增长率（%）
     */
    @Schema(description = "环比增长率（%）")
    private BigDecimal growthRate;
}
