package com.edu.finance.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课消报表导出DTO
 */
@Data
public class ClassHourReportExportDTO {

    @ExcelProperty("统计维度")
    private String dimension;

    @ExcelProperty("维度值")
    private String dimensionValue;

    @ExcelProperty("总课时")
    private BigDecimal totalHours;

    @ExcelProperty("已用课时")
    private BigDecimal usedHours;

    @ExcelProperty("剩余课时")
    private BigDecimal remainingHours;

    @ExcelProperty("课消率（%）")
    private BigDecimal consumptionRate;

    @ExcelProperty("课消金额（元）")
    private BigDecimal consumptionAmount;

    @ExcelProperty("学员数量")
    private Integer studentCount;

    @ExcelProperty("班级数量")
    private Integer classCount;
}
