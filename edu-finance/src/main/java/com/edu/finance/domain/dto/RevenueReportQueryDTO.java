package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 收入报表查询DTO
 */
@Data
@Schema(description = "收入报表查询DTO")
public class RevenueReportQueryDTO {

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 时间维度：day-日，week-周，month-月，quarter-季，year-年
     */
    @Schema(description = "时间维度：day-日，week-周，month-月，quarter-季，year-年")
    private String timeDimension;

    /**
     * 课程类型ID
     */
    @Schema(description = "课程类型ID")
    private Long courseTypeId;

    /**
     * 支付方式
     */
    @Schema(description = "支付方式")
    private String paymentMethod;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortField;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    @Schema(description = "排序方式：asc-升序，desc-降序")
    private String sortOrder;
}
