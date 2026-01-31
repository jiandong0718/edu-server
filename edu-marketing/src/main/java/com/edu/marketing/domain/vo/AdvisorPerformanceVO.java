package com.edu.marketing.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 顾问业绩统计VO
 *
 * @author edu
 * @since 2024-01-30
 */
@Data
@Schema(description = "顾问业绩统计")
public class AdvisorPerformanceVO {

    @Schema(description = "顾问ID")
    private Long advisorId;

    @Schema(description = "顾问姓名")
    private String advisorName;

    @Schema(description = "线索总数")
    private Integer totalLeadCount;

    @Schema(description = "跟进次数")
    private Integer followUpCount;

    @Schema(description = "预约数量")
    private Integer appointmentCount;

    @Schema(description = "试听数量")
    private Integer trialCount;

    @Schema(description = "成交数量")
    private Integer conversionCount;

    @Schema(description = "成交金额")
    private BigDecimal conversionAmount;

    @Schema(description = "转化率")
    private BigDecimal conversionRate;

    @Schema(description = "平均跟进次数")
    private BigDecimal avgFollowUpCount;
}
