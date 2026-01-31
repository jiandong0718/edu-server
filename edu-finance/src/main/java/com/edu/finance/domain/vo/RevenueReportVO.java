package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收入报表VO
 */
@Data
@Schema(description = "收入报表VO")
public class RevenueReportVO {

    /**
     * 统计维度值（日期、校区名称、课程类型名称、支付方式名称等）
     */
    @Schema(description = "统计维度值")
    private String dimensionValue;

    /**
     * 维度（别名，用于兼容）
     */
    @Schema(description = "维度")
    public String getDimension() {
        return dimensionValue;
    }

    /**
     * 收入金额
     */
    @Schema(description = "收入金额")
    private BigDecimal revenueAmount;

    /**
     * 总收入（别名，用于兼容）
     */
    @Schema(description = "总收入")
    public BigDecimal getTotalRevenue() {
        return revenueAmount;
    }

    /**
     * 新签合同收入
     */
    @Schema(description = "新签合同收入")
    private BigDecimal newContractRevenue;

    /**
     * 续费收入
     */
    @Schema(description = "续费收入")
    private BigDecimal renewalRevenue;

    /**
     * 退费金额
     */
    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    /**
     * 净收入
     */
    @Schema(description = "净收入")
    private BigDecimal netRevenue;

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
    private BigDecimal avgOrderAmount;

    /**
     * 平均订单价值（别名，用于兼容）
     */
    @Schema(description = "平均订单价值")
    public BigDecimal getAvgOrderValue() {
        return avgOrderAmount;
    }

    /**
     * 环比增长率（%）
     */
    @Schema(description = "环比增长率（%）")
    private BigDecimal growthRate;

    /**
     * 占比（%）
     */
    @Schema(description = "占比（%）")
    private BigDecimal percentage;
}
