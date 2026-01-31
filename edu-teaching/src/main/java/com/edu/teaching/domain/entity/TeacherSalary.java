package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 教师课酬配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tch_teacher_salary_config")
public class TeacherSalary extends BaseEntity {

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 课程ID（为空表示默认课酬）
     */
    private Long courseId;

    /**
     * 班级类型：one_to_one-一对一，small_class-小班课，large_class-大班课
     */
    private String classType;

    /**
     * 课酬类型：per_hour-按课时，per_class-按课次，fixed-固定
     */
    private String salaryType;

    /**
     * 课酬金额
     */
    private BigDecimal amount;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 失效日期
     */
    private LocalDate expiryDate;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
