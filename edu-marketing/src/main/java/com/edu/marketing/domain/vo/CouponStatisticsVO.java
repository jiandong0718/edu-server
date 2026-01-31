package com.edu.marketing.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券统计VO
 */
@Data
public class CouponStatisticsVO {

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型
     */
    private String couponType;

    /**
     * 发行总量
     */
    private Integer totalQuantity;

    /**
     * 已发放数量
     */
    private Integer issuedQuantity;

    /**
     * 已使用数量
     */
    private Integer usedQuantity;

    /**
     * 未使用数量
     */
    private Integer unusedQuantity;

    /**
     * 已过期数量
     */
    private Integer expiredQuantity;

    /**
     * 使用率（百分比）
     */
    private BigDecimal useRate;

    /**
     * 总优惠金额
     */
    private BigDecimal totalDiscountAmount;

    /**
     * 平均优惠金额
     */
    private BigDecimal avgDiscountAmount;
}
