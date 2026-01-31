package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课时消课规则实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_class_hour_rule")
public class ClassHourRule extends BaseEntity {

    /**
     * 规则名称
     */
    private String name;

    /**
     * 课程ID（为空表示通用规则）
     */
    private Long courseId;

    /**
     * 班级类型：one_on_one-一对一，small_class-小班课，large_class-大班课
     */
    private String classType;

    /**
     * 扣减类型：per_hour-按课时，per_class-按课次，custom-自定义
     */
    private String deductType;

    /**
     * 扣减数量
     */
    private BigDecimal deductAmount;

    /**
     * 状态：active-启用，inactive-停用
     */
    private String status;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 备注
     */
    private String remark;
}
