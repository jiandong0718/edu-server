package com.edu.teaching.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格策略规则实体
 */
@Data
@TableName("tch_price_strategy_rule")
public class PriceStrategyRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 策略ID
     */
    private Long strategyId;

    /**
     * 条件类型：CLASS_HOURS-课时数, AMOUNT-金额, MEMBER_LEVEL-会员等级
     */
    private String conditionType;

    /**
     * 条件值（JSON格式）
     */
    private String conditionValue;

    /**
     * 折扣类型：PERCENTAGE-百分比, FIXED-固定金额, PRICE-直接定价
     */
    private String discountType;

    /**
     * 折扣值
     */
    private BigDecimal discountValue;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
