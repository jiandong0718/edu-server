package com.edu.teaching.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格策略规则DTO
 */
@Data
public class PriceStrategyRuleDTO {

    /**
     * 规则ID（更新时需要）
     */
    private Long id;

    /**
     * 条件类型：CLASS_HOURS-课时数, AMOUNT-金额, MEMBER_LEVEL-会员等级
     */
    @NotBlank(message = "条件类型不能为空")
    private String conditionType;

    /**
     * 条件值（JSON格式）
     */
    @NotBlank(message = "条件值不能为空")
    private String conditionValue;

    /**
     * 折扣类型：PERCENTAGE-百分比, FIXED-固定金额, PRICE-直接定价
     */
    @NotBlank(message = "折扣类型不能为空")
    private String discountType;

    /**
     * 折扣值
     */
    @NotNull(message = "折扣值不能为空")
    private BigDecimal discountValue;
}
