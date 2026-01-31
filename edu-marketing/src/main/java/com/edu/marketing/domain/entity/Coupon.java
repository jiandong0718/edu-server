package com.edu.marketing.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mkt_coupon")
public class Coupon extends BaseEntity {

    /**
     * 优惠券编号
     */
    private String couponNo;

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
     * 已发放数量
     */
    private Integer issuedQuantity;

    /**
     * 已使用数量
     */
    private Integer usedQuantity;

    /**
     * 状态：draft-草稿，active-生效中，paused-已暂停，expired-已过期
     */
    private String status;

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
     * 适用校区ID列表（逗号分隔，空表示全部）
     */
    private String campusIds;

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
}
