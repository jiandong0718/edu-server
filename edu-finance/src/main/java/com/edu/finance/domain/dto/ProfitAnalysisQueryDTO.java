package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 利润分析查询DTO
 */
@Data
@Schema(description = "利润分析查询DTO")
public class ProfitAnalysisQueryDTO {

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 课程类型ID
     */
    @Schema(description = "课程类型ID")
    private Long courseTypeId;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期", required = true)
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期", required = true)
    private LocalDate endDate;

    /**
     * 分析维度：campus-按校区，course_type-按课程类型，month-按月份
     */
    @Schema(description = "分析维度", example = "campus")
    private String dimension;
}
