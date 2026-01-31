package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课时汇总统计VO
 */
@Data
@Schema(description = "课时汇总统计VO")
public class ClassHourSummaryVO {

    /**
     * 总账户数
     */
    @Schema(description = "总账户数")
    private Integer totalAccounts;

    /**
     * 活跃账户数
     */
    @Schema(description = "活跃账户数")
    private Integer activeAccounts;

    /**
     * 冻结账户数
     */
    @Schema(description = "冻结账户数")
    private Integer frozenAccounts;

    /**
     * 已用完账户数
     */
    @Schema(description = "已用完账户数")
    private Integer exhaustedAccounts;

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
     * 冻结课时
     */
    @Schema(description = "冻结课时")
    private BigDecimal frozenHours;

    /**
     * 课时使用率（%）
     */
    @Schema(description = "课时使用率")
    private BigDecimal usageRate;

    /**
     * 预警账户数
     */
    @Schema(description = "预警账户数")
    private Integer warningAccounts;

    /**
     * 余额不足预警数
     */
    @Schema(description = "余额不足预警数")
    private Integer lowBalanceWarnings;

    /**
     * 即将过期预警数
     */
    @Schema(description = "即将过期预警数")
    private Integer expiringWarnings;

    /**
     * 已过期预警数
     */
    @Schema(description = "已过期预警数")
    private Integer expiredWarnings;
}
