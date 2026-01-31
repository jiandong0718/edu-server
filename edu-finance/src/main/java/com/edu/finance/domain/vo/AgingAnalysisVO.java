package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 账龄分析VO
 */
@Data
@Schema(description = "账龄分析VO")
public class AgingAnalysisVO {

    /**
     * 30天内欠费金额
     */
    @Schema(description = "30天内欠费金额")
    private BigDecimal within30DaysAmount;

    /**
     * 30天内欠费合同数
     */
    @Schema(description = "30天内欠费合同数")
    private Integer within30DaysCount;

    /**
     * 30-60天欠费金额
     */
    @Schema(description = "30-60天欠费金额")
    private BigDecimal days30To60Amount;

    /**
     * 30-60天欠费合同数
     */
    @Schema(description = "30-60天欠费合同数")
    private Integer days30To60Count;

    /**
     * 60-90天欠费金额
     */
    @Schema(description = "60-90天欠费金额")
    private BigDecimal days60To90Amount;

    /**
     * 60-90天欠费合同数
     */
    @Schema(description = "60-90天欠费合同数")
    private Integer days60To90Count;

    /**
     * 90天以上欠费金额
     */
    @Schema(description = "90天以上欠费金额")
    private BigDecimal over90DaysAmount;

    /**
     * 90天以上欠费合同数
     */
    @Schema(description = "90天以上欠费合同数")
    private Integer over90DaysCount;

    /**
     * 总欠费金额
     */
    @Schema(description = "总欠费金额")
    private BigDecimal totalArrearsAmount;

    /**
     * 总欠费合同数
     */
    @Schema(description = "总欠费合同数")
    private Integer totalArrearsCount;
}
