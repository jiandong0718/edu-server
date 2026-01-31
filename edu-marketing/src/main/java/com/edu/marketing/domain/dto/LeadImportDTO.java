package com.edu.marketing.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 线索导入DTO
 */
@Data
public class LeadImportDTO {

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
     * 年龄
     */
    @ExcelProperty(value = "年龄", index = 2)
    private String age;

    /**
     * 手机号（必填，11位）
     */
    @ExcelProperty(value = "手机号", index = 3)
    private String phone;

    /**
     * 来源（必填：offline-地推，referral-转介绍，online_ad-线上广告，walk_in-自然到访，phone-电话咨询）
     */
    @ExcelProperty(value = "来源", index = 4)
    private String source;

    /**
     * 来源详情
     */
    @ExcelProperty(value = "来源详情", index = 5)
    private String sourceDetail;

    /**
     * 意向程度（high-高，medium-中，low-低）
     */
    @ExcelProperty(value = "意向程度", index = 6)
    private String intentLevel;

    /**
     * 就读学校
     */
    @ExcelProperty(value = "就读学校", index = 7)
    private String school;

    /**
     * 年级
     */
    @ExcelProperty(value = "年级", index = 8)
    private String grade;

    /**
     * 意向课程
     */
    @ExcelProperty(value = "意向课程", index = 9)
    private String intentCourse;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 10)
    private String remark;
}
