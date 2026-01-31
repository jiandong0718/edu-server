package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课消趋势VO
 */
@Data
@Schema(description = "课消趋势VO")
public class ClassHourTrendVO {

    /**
     * 日期
     */
    @Schema(description = "日期")
    private String date;

    /**
     * 消课课时数
     */
    @Schema(description = "消课课时数")
    private BigDecimal consumedHours;

    /**
     * 消课次数
     */
    @Schema(description = "消课次数")
    private Integer consumptionCount;

    /**
     * 学员数量
     */
    @Schema(description = "学员数量")
    private Integer studentCount;

    /**
     * 课消率（%）
     */
    @Schema(description = "课消率（%）")
    private BigDecimal consumptionRate;
}
