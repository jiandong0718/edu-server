package com.edu.marketing.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券发放记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mkt_coupon_record")
public class CouponRecord extends BaseEntity {

    /**
     * 记录编号
     */
    private String recordNo;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券编号
     */
    private String couponNo;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 学员姓名
     */
    private String studentName;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 状态：unused-未使用，used-已使用，expired-已过期，invalid-已失效
     */
    private String status;

    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;

    /**
     * 领取方式：manual-手动发放，auto-自动领取，system-系统赠送
     */
    private String receiveType;

    /**
     * 有效开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 有效结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 使用合同ID
     */
    private Long useContractId;

    /**
     * 使用收款ID
     */
    private Long usePaymentId;

    /**
     * 实际优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 失效原因
     */
    private String invalidReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 校区名称（非数据库字段）
     */
    @TableField(exist = false)
    private String campusName;

    /**
     * 优惠券类型（非数据库字段）
     */
    @TableField(exist = false)
    private String couponType;

    /**
     * 优惠值（非数据库字段）
     */
    @TableField(exist = false)
    private BigDecimal couponDiscountValue;
}
