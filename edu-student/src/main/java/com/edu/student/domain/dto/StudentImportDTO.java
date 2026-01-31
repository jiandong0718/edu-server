package com.edu.student.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学员导入DTO
 */
@Data
public class StudentImportDTO {

    /**
     * 姓名（必填）
     */
    @ExcelProperty(value = "姓名", index = 0)
    private String name;

    /**
     * 性别（必填：男/女）
     */
    @ExcelProperty(value = "性别", index = 1)
    private String gender;

    /**
     * 出生日期（必填：yyyy-MM-dd）
     */
    @ExcelProperty(value = "出生日期", index = 2)
    @DateTimeFormat("yyyy-MM-dd")
    private String birthday;

    /**
     * 手机号（必填，11位）
     */
    @ExcelProperty(value = "手机号", index = 3)
    private String phone;

    /**
     * 身份证号
     */
    @ExcelProperty(value = "身份证号", index = 4)
    private String idCard;

    /**
     * 就读学校
     */
    @ExcelProperty(value = "就读学校", index = 5)
    private String school;

    /**
     * 年级
     */
    @ExcelProperty(value = "年级", index = 6)
    private String grade;

    /**
     * 联系人姓名
     */
    @ExcelProperty(value = "联系人姓名", index = 7)
    private String contactName;

    /**
     * 联系人关系（父亲/母亲/其他）
     */
    @ExcelProperty(value = "联系人关系", index = 8)
    private String contactRelation;

    /**
     * 联系人电话
     */
    @ExcelProperty(value = "联系人电话", index = 9)
    private String contactPhone;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址", index = 10)
    private String address;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 11)
    private String remark;
}
