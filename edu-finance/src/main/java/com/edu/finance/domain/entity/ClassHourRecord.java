package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课时消耗记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_class_hour_record")
public class ClassHourRecord extends BaseEntity {

    /**
     * 课时账户ID
     */
    private Long accountId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 类型：consume-消耗，gift-赠送，adjust-调整，refund-退费
     */
    private String type;

    /**
     * 课时数（正数增加，负数减少）
     */
    private BigDecimal hours;

    /**
     * 变动后余额
     */
    private BigDecimal balance;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间（仅插入时填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private java.time.LocalDateTime createTime;

    /**
     * 创建人ID（仅插入时填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
}
