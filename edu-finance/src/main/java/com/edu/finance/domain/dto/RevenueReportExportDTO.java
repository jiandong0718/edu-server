package com.edu.finance.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收入报表导出DTO
 */
@Data
public class RevenueReportExportDTO {

    @ExcelProperty("统计维度")
    private String dimension;

    @ExcelProperty("维度值")
    private String dimensionValue;

    @ExcelProperty("总收入（元）")
    private BigDecimal totalRevenue;

    @ExcelProperty("新签收入（元）")
    private BigDecimal newContractRevenue;

    @ExcelProperty("续费收入（元）")
    private BigDecimal renewalRevenue;

    @ExcelProperty("退费金额（元）")
    private BigDecimal refundAmount;

    @ExcelProperty("实际收入（元）")
    private BigDecimal netRevenue;

    @ExcelProperty("合同数量")
    private Integer contractCount;

    @ExcelProperty("学员数量")
    private Integer studentCount;

    @ExcelProperty("平均客单价（元）")
    private BigDecimal avgOrderValue;
}
