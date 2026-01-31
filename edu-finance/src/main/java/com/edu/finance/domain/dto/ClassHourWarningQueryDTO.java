package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课时预警查询DTO
 */
@Data
@Schema(description = "课时预警查询DTO")
public class ClassHourWarningQueryDTO {

    /**
     * 预警类型：low_balance-余额不足，expiring-即将过期，expired-已过期
     */
    @Schema(description = "预警类型：low_balance-余额不足，expiring-即将过期，expired-已过期")
    private String warningType;

    /**
     * 预警级别：normal-正常，warning-警告，urgent-紧急
     */
    @Schema(description = "预警级别：normal-正常，warning-警告，urgent-紧急")
    private String warningLevel;

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseId;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 余额不足阈值（默认5课时）
     */
    @Schema(description = "余额不足阈值（默认5课时）")
    private BigDecimal lowBalanceThreshold;

    /**
     * 即将过期天数阈值（默认30天）
     */
    @Schema(description = "即将过期天数阈值（默认30天）")
    private Integer expiringDaysThreshold;
}
