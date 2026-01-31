package com.edu.teaching.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格策略规则响应VO
 */
@Data
public class PriceStrategyRuleVO {

    /**
     * 规则ID
     */
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
     * 条件类型名称
     */
    private String conditionTypeName;

    /**
     * 条件值（JSON格式）
     */
    private String conditionValue;

    /**
     * 条件描述（解析后的可读文本）
     */
    private String conditionDescription;

    /**
     * 折扣类型：PERCENTAGE-百分比, FIXED-固定金额, PRICE-直接定价
     */
    private String discountType;

    /**
     * 折扣类型名称
     */
    private String discountTypeName;

    /**
     * 折扣值
     */
    private BigDecimal discountValue;

    /**
     * 折扣描述
     */
    private String discountDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
