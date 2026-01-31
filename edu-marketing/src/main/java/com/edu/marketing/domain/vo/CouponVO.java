package com.edu.marketing.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券详情VO
 */
@Data
public class CouponVO {

    /**
     * 优惠券ID
     */
    private Long id;

    /**
     * 优惠券编号
     */
    private String couponNo;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型
     */
    private String type;

    /**
     * 折扣类型
     */
    private String discountType;

    /**
     * 优惠值
     */
    private BigDecimal discountValue;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 最大优惠金额
     */
    private BigDecimal maxDiscountAmount;

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
     * 剩余数量
     */
    private Integer remainingQuantity;

    /**
     * 状态
     */
    private String status;

    /**
     * 有效期类型
     */
    private String validType;

    /**
     * 有效开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 有效结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 有效天数
     */
    private Integer validDays;

    /**
     * 领取方式
     */
    private String receiveType;

    /**
     * 每人限领数量
     */
    private Integer receiveLimit;

    /**
     * 每人限用数量
     */
    private Integer useLimit;

    /**
     * 适用校区ID列表
     */
    private String campusIds;

    /**
     * 适用校区名称列表
     */
    private List<String> campusNames;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 使用规则说明
     */
    private String usageRules;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 使用规则列表
     */
    private List<CouponRuleVO> rules;
}
