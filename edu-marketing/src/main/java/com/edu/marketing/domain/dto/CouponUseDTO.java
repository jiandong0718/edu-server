package com.edu.marketing.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券使用DTO
 */
@Data
public class CouponUseDTO {

    /**
     * 优惠券记录ID
     */
    private Long recordId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 收款ID
     */
    private Long paymentId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 课程ID列表（用于验证规则）
     */
    private String courseIds;

    /**
     * 合同类型（用于验证规则）
     */
    private String contractType;
}
