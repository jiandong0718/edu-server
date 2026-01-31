package com.edu.marketing.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券验证结果VO
 */
@Data
public class CouponValidateVO {

    /**
     * 是否可用
     */
    private Boolean valid;

    /**
     * 不可用原因
     */
    private String reason;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 优惠券记录ID
     */
    private Long recordId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型
     */
    private String couponType;

    /**
     * 优惠值
     */
    private BigDecimal discountValue;

    /**
     * 折扣类型
     */
    private String discountType;
}
