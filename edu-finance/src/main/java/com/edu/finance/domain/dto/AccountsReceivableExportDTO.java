package com.edu.finance.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 应收账款报表导出DTO
 */
@Data
public class AccountsReceivableExportDTO {

    @ExcelProperty("学员姓名")
    private String studentName;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("所属校区")
    private String campusName;

    @ExcelProperty("合同编号")
    private String contractNo;

    @ExcelProperty("合同金额（元）")
    private BigDecimal contractAmount;

    @ExcelProperty("已收金额（元）")
    private BigDecimal paidAmount;

    @ExcelProperty("应收金额（元）")
    private BigDecimal receivableAmount;

    @ExcelProperty("账龄（天）")
    private Integer agingDays;

    @ExcelProperty("账龄分类")
    private String agingCategory;

    @ExcelProperty("签约日期")
    private String contractDate;

    @ExcelProperty("最后收款日期")
    private String lastPaymentDate;
}
