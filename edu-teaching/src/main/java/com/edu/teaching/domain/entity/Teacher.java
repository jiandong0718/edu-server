package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_teacher")
public class Teacher extends BaseEntity {

    /**
     * 教师编号
     */
    private String teacherNo;

    /**
     * 教师姓名
     */
    private String name;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 入职日期
     */
    private java.time.LocalDate entryDate;

    /**
     * 教龄（年）
     */
    private Integer teachingYears;

    /**
     * 学历
     */
    private String education;

    /**
     * 毕业院校
     */
    private String graduateSchool;

    /**
     * 专业
     */
    private String major;

    /**
     * 教师简介
     */
    private String introduction;

    /**
     * 状态：active-在职，leave-休假，resigned-离职
     */
    private String status;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 备注
     */
    private String remark;
}
