package com.edu.marketing.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券创建DTO
 */
@Data
public class CouponCreateDTO {

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券类型：full_reduction-满减券，discount-折扣券，cash-代金券
     */
    private String type;

    /**
     * 折扣类型：amount-金额，percent-百分比
     */
    private String discountType;

    /**
     * 优惠值（金额或折扣百分比）
     */
    private BigDecimal discountValue;

    /**
     * 最低消费金额（满减条件）
     */
    private BigDecimal minAmount;

    /**
     * 最大优惠金额（折扣券封顶）
     */
    private BigDecimal maxDiscountAmount;

    /**
     * 发行总量
     */
    private Integer totalQuantity;

    /**
     * 有效期类型：fixed-固定时间，relative-相对天数
     */
    private String validType;

    /**
     * 有效开始时间（固定时间）
     */
    private LocalDateTime validStartTime;

    /**
     * 有效结束时间（固定时间）
     */
    private LocalDateTime validEndTime;

    /**
     * 有效天数（相对天数，从领取日起）
     */
    private Integer validDays;

    /**
     * 领取方式：manual-手动发放，auto-自动领取
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
    private List<Long> campusIds;

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
     * 使用规则列表
     */
    private List<CouponRuleDTO> rules;
}
