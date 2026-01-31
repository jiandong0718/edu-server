package com.edu.finance.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课时消课统计VO
 */
@Data
@Schema(description = "课时消课统计VO")
public class ClassHourConsumptionVO {

    /**
     * 统计日期（格式：yyyy-MM-dd）
     */
    @Schema(description = "统计日期")
    private String statisticsDate;

    /**
     * 消课次数
     */
    @Schema(description = "消课次数")
    private Integer consumptionCount;

    /**
     * 消课课时数
     */
    @Schema(description = "消课课时数")
    private BigDecimal consumptionHours;

    /**
     * 涉及学员数
     */
    @Schema(description = "涉及学员数")
    private Integer studentCount;

    /**
     * 涉及课程数
     */
    @Schema(description = "涉及课程数")
    private Integer courseCount;

    /**
     * 平均每次消课课时
     */
    @Schema(description = "平均每次消课课时")
    private BigDecimal avgHoursPerConsumption;
}
