package com.edu.student.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * 学员实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stu_student")
public class Student extends BaseEntity {

    /**
     * 学员编号
     */
    private String studentNo;

    /**
     * 学员姓名
     */
    private String name;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 学校
     */
    private String school;

    /**
     * 年级
     */
    private String grade;

    /**
     * 状态：potential-潜在，trial-试听，enrolled-在读，suspended-休学，graduated-结业，refunded-退费
     */
    private String status;

    /**
     * 来源：offline-地推，referral-转介绍，online_ad-线上广告，walk_in-自然到访，phone-电话咨询
     */
    private String source;

    /**
     * 所属校区ID
     */
    private Long campusId;

    /**
     * 跟进顾问ID
     */
    private Long advisorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 联系人列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<StudentContact> contacts;

    /**
     * 标签列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<String> tags;

    /**
     * 校区名称（非数据库字段）
     */
    @TableField(exist = false)
    private String campusName;

    /**
     * 顾问姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String advisorName;
}
