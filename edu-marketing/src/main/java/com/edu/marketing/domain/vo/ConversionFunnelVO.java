package com.edu.marketing.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 招生转化漏斗统计VO
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@Schema(description = "招生转化漏斗统计")
public class ConversionFunnelVO {

    @Schema(description = "新线索数量")
    private Integer newLeadCount;

    @Schema(description = "跟进中数量")
    private Integer followingCount;

    @Schema(description = "已预约数量")
    private Integer appointedCount;

    @Schema(description = "已试听数量")
    private Integer trialedCount;

    @Schema(description = "已成交数量")
    private Integer convertedCount;

    @Schema(description = "已流失数量")
    private Integer lostCount;

    @Schema(description = "预约转化率")
    private BigDecimal appointmentRate;

    @Schema(description = "试听转化率")
    private BigDecimal trialRate;

    @Schema(description = "成交转化率")
    private BigDecimal conversionRate;

    @Schema(description = "整体转化率")
    private BigDecimal overallRate;
}
