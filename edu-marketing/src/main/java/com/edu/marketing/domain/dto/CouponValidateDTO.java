package com.edu.marketing.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券验证DTO
 */
@Data
public class CouponValidateDTO {

    /**
     * 优惠券记录ID
     */
    private Long recordId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 课程ID列表（逗号分隔）
     */
    private String courseIds;

    /**
     * 合同类型
     */
    private String contractType;

    /**
     * 校区ID
     */
    private Long campusId;
}
