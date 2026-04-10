package com.edu.marketing.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 线索导出DTO
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(25)
public class LeadExportDTO {

    @ExcelProperty(value = "线索编号", index = 0)
    @ColumnWidth(20)
    private String leadNo;

    @ExcelProperty(value = "姓名", index = 1)
    @ColumnWidth(15)
    private String name;

    @ExcelProperty(value = "手机号", index = 2)
    @ColumnWidth(18)
    private String phone;

    @ExcelProperty(value = "性别", index = 3)
    @ColumnWidth(10)
    private String gender;

    @ExcelProperty(value = "年龄", index = 4)
    @ColumnWidth(10)
    private Integer age;

    @ExcelProperty(value = "来源", index = 5)
    @ColumnWidth(15)
    private String source;

    @ExcelProperty(value = "来源详情", index = 6)
    @ColumnWidth(20)
    private String sourceDetail;

    @ExcelProperty(value = "学校", index = 7)
    @ColumnWidth(18)
    private String school;

    @ExcelProperty(value = "年级", index = 8)
    @ColumnWidth(12)
    private String grade;

    @ExcelProperty(value = "意向程度", index = 9)
    @ColumnWidth(12)
    private String intentLevel;

    @ExcelProperty(value = "状态", index = 10)
    @ColumnWidth(12)
    private String status;

    @ExcelProperty(value = "顾问", index = 11)
    @ColumnWidth(15)
    private String advisorName;

    @ExcelProperty(value = "校区", index = 12)
    @ColumnWidth(15)
    private String campusName;

    @ExcelProperty(value = "跟进次数", index = 13)
    @ColumnWidth(12)
    private Integer followCount;

    @ExcelProperty(value = "最后跟进时间", index = 14)
    @ColumnWidth(20)
    private String lastFollowTime;

    @ExcelProperty(value = "创建时间", index = 15)
    @ColumnWidth(20)
    private String createTime;

    @ExcelProperty(value = "备注", index = 16)
    @ColumnWidth(30)
    private String remark;
}
