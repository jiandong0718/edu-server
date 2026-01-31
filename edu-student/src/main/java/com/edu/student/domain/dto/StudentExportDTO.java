package com.edu.student.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 学员导出DTO
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(25)
public class StudentExportDTO {

    /**
     * 学员编号
     */
    @ExcelProperty(value = "学员编号", index = 0)
    @ColumnWidth(20)
    private String studentNo;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 1)
    @ColumnWidth(15)
    private String name;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 2)
    @ColumnWidth(10)
    private String gender;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "出生日期", index = 3)
    @ColumnWidth(15)
    private String birthday;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 4)
    @ColumnWidth(15)
    private String phone;

    /**
     * 身份证号
     */
    @ExcelProperty(value = "身份证号", index = 5)
    @ColumnWidth(20)
    private String idCard;

    /**
     * 就读学校
     */
    @ExcelProperty(value = "就读学校", index = 6)
    @ColumnWidth(20)
    private String school;

    /**
     * 年级
     */
    @ExcelProperty(value = "年级", index = 7)
    @ColumnWidth(12)
    private String grade;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", index = 8)
    @ColumnWidth(12)
    private String status;

    /**
     * 来源
     */
    @ExcelProperty(value = "来源", index = 9)
    @ColumnWidth(15)
    private String source;

    /**
     * 校区
     */
    @ExcelProperty(value = "校区", index = 10)
    @ColumnWidth(15)
    private String campusName;

    /**
     * 顾问
     */
    @ExcelProperty(value = "顾问", index = 11)
    @ColumnWidth(15)
    private String advisorName;

    /**
     * 联系人姓名
     */
    @ExcelProperty(value = "联系人姓名", index = 12)
    @ColumnWidth(15)
    private String contactName;

    /**
     * 联系人关系
     */
    @ExcelProperty(value = "联系人关系", index = 13)
    @ColumnWidth(12)
    private String contactRelation;

    /**
     * 联系人电话
     */
    @ExcelProperty(value = "联系人电话", index = 14)
    @ColumnWidth(15)
    private String contactPhone;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址", index = 15)
    @ColumnWidth(30)
    private String address;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 16)
    @ColumnWidth(30)
    private String remark;
}
