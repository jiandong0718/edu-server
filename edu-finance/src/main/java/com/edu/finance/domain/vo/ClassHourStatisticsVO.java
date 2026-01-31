package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课时统计VO
 */
@Data
@Schema(description = "课时统计VO")
public class ClassHourStatisticsVO {

    /**
     * 统计维度：student-学员，course-课程，campus-校区
     */
    @Schema(description = "统计维度")
    private String dimension;

    /**
     * 维度ID
     */
    @Schema(description = "维度ID")
    private Long dimensionId;

    /**
     * 维度名称
     */
    @Schema(description = "维度名称")
    private String dimensionName;

    /**
     * 账户数量
     */
    @Schema(description = "账户数量")
    private Integer accountCount;

    /**
     * 总课时
     */
    @Schema(description = "总课时")
    private BigDecimal totalHours;

    /**
     * 已消耗课时
     */
    @Schema(description = "已消耗课时")
    private BigDecimal usedHours;

    /**
     * 剩余课时
     */
    @Schema(description = "剩余课时")
    private BigDecimal remainingHours;

    /**
     * 赠送课时
     */
    @Schema(description = "赠送课时")
    private BigDecimal giftHours;

    /**
     * 课时使用率（%）
     */
    @Schema(description = "课时使用率")
    private BigDecimal usageRate;

    /**
     * 预警账户数
     */
    @Schema(description = "预警账户数")
    private Integer warningAccountCount;
}
