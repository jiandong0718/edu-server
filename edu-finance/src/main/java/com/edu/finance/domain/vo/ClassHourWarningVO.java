package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课时预警VO
 */
@Data
@Schema(description = "课时预警VO")
public class ClassHourWarningVO {

    /**
     * 账户ID
     */
    @Schema(description = "账户ID")
    private Long accountId;

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 学员姓名
     */
    @Schema(description = "学员姓名")
    private String studentName;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称")
    private String courseName;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String campusName;

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
     * 冻结课时
     */
    @Schema(description = "冻结课时")
    private BigDecimal frozenHours;

    /**
     * 到期日期
     */
    @Schema(description = "到期日期")
    private LocalDate expiryDate;

    /**
     * 距离到期天数
     */
    @Schema(description = "距离到期天数")
    private Integer daysToExpiry;

    /**
     * 预警类型：low_balance-余额不足，expiring-即将过期，expired-已过期
     */
    @Schema(description = "预警类型")
    private String warningType;

    /**
     * 预警级别：normal-正常，warning-警告，urgent-紧急
     */
    @Schema(description = "预警级别")
    private String warningLevel;

    /**
     * 预警消息
     */
    @Schema(description = "预警消息")
    private String warningMessage;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
