package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课时余额VO
 */
@Data
@Schema(description = "课时余额VO")
public class ClassHourBalanceVO {

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
     * 可用课时（剩余课时 - 冻结课时）
     */
    @Schema(description = "可用课时")
    private BigDecimal availableHours;

    /**
     * 状态：active-正常，frozen-冻结，exhausted-已用完
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述")
    private String statusDesc;

    /**
     * 是否预警
     */
    @Schema(description = "是否预警")
    private Boolean isWarning;

    /**
     * 预警原因
     */
    @Schema(description = "预警原因")
    private String warningReason;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
