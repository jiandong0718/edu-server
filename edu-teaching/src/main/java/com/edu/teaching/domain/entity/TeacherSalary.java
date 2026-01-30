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
@TableName("tch_teacher_salary")
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
     * 课酬类型：1-按课时，2-按天，3-按月
     */
    private Integer salaryType;

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
