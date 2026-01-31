package com.edu.teaching.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 价格计算结果VO
 */
@Data
public class PriceCalculateVO {

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 最终价格
     */
    private BigDecimal finalPrice;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 折扣率（百分比）
     */
    private BigDecimal discountRate;

    /**
     * 应用的策略列表
     */
    private List<AppliedStrategyVO> appliedStrategies;

    /**
     * 计算说明
     */
    private String description;

    /**
     * 应用的策略信息
     */
    @Data
    public static class AppliedStrategyVO {
        /**
         * 策略ID
         */
        private Long strategyId;

        /**
         * 策略名称
         */
        private String strategyName;

        /**
         * 策略类型
         */
        private String strategyType;

        /**
         * 优先级
         */
        private Integer priority;

        /**
         * 应用的规则ID
         */
        private Long ruleId;

        /**
         * 规则描述
         */
        private String ruleDescription;

        /**
         * 折扣金额
         */
        private BigDecimal discountAmount;
    }
}
