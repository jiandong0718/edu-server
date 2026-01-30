package com.edu.finance.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edu.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收款记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_payment")
public class Payment extends BaseEntity {

    /**
     * 收款单号
     */
    private String paymentNo;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 收款金额
     */
    private BigDecimal amount;

    /**
     * 支付方式：wechat-微信，alipay-支付宝，unionpay-银联，cash-现金，pos-POS机
     */
    private String paymentMethod;

    /**
     * 支付状态：pending-待支付，paid-已支付，failed-支付失败，refunded-已退款
     */
    private String status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 第三方交易号
     */
    private String transactionNo;

    /**
     * 收款人ID
     */
    private Long receiverId;

    /**
     * 备注
     */
    private String remark;
}
