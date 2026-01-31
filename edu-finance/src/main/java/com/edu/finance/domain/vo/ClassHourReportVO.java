package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课消报表VO
 */
@Data
@Schema(description = "课消报表VO")
public class ClassHourReportVO {

    /**
     * 统计维度值（班级名称、课程名称、教师名称等）
     */
    @Schema(description = "统计维度值")
    private String dimensionValue;

    /**
     * 维度（别名，用于兼容）
     */
    @Schema(description = "维度")
    public String getDimension() {
        return dimensionValue;
    }

    /**
     * 维度ID
     */
    @Schema(description = "维度ID")
    private Long dimensionId;

    /**
     * 总课时数
     */
    @Schema(description = "总课时数")
    private BigDecimal totalHours;

    /**
     * 已消课时数
     */
    @Schema(description = "已消课时数")
    private BigDecimal consumedHours;

    /**
     * 已用课时（别名，用于兼容）
     */
    @Schema(description = "已用课时")
    public BigDecimal getUsedHours() {
        return consumedHours;
    }

    /**
     * 剩余课时数
     */
    @Schema(description = "剩余课时数")
    private BigDecimal remainingHours;

    /**
     * 课消率（%）
     */
    @Schema(description = "课消率（%）")
    private BigDecimal consumptionRate;

    /**
     * 学员数量
     */
    @Schema(description = "学员数量")
    private Integer studentCount;

    /**
     * 消课次数
     */
    @Schema(description = "消课次数")
    private Integer consumptionCount;

    /**
     * 消课金额
     */
    @Schema(description = "消课金额")
    private BigDecimal consumptionAmount;

    /**
     * 班级数量（别名，用于兼容）
     */
    @Schema(description = "班级数量")
    public Integer getClassCount() {
        return consumptionCount;
    }

    /**
     * 平均每次消课课时
     */
    @Schema(description = "平均每次消课课时")
    private BigDecimal avgHoursPerConsumption;
}
