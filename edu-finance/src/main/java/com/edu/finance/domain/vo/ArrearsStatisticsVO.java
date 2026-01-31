package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 欠费统计VO
 */
@Data
@Schema(description = "欠费统计VO")
public class ArrearsStatisticsVO {

    /**
     * 总欠费金额
     */
    @Schema(description = "总欠费金额")
    private BigDecimal totalArrearsAmount;

    /**
     * 欠费人数
     */
    @Schema(description = "欠费人数")
    private Integer arrearsStudentCount;

    /**
     * 欠费合同数
     */
    @Schema(description = "欠费合同数")
    private Integer arrearsContractCount;

    /**
     * 平均欠费金额
     */
    @Schema(description = "平均欠费金额")
    private BigDecimal avgArrearsAmount;

    /**
     * 最大欠费金额
     */
    @Schema(description = "最大欠费金额")
    private BigDecimal maxArrearsAmount;

    /**
     * 欠费7天以内的合同数
     */
    @Schema(description = "欠费7天以内的合同数")
    private Integer arrears7DaysCount;

    /**
     * 欠费7-15天的合同数
     */
    @Schema(description = "欠费7-15天的合同数")
    private Integer arrears7To15DaysCount;

    /**
     * 欠费15-30天的合同数
     */
    @Schema(description = "欠费15-30天的合同数")
    private Integer arrears15To30DaysCount;

    /**
     * 欠费30天以上的合同数
     */
    @Schema(description = "欠费30天以上的合同数")
    private Integer arrears30PlusDaysCount;
}
