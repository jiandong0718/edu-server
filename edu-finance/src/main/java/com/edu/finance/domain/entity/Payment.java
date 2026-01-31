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
     * 支付渠道订单号
     */
    private String channelOrderNo;

    /**
     * 支付场景：app-APP支付, h5-H5支付, native-扫码支付, jsapi-公众号/小程序支付
     */
    private String paymentScene;

    /**
     * 收款人ID
     */
    private Long receiverId;

    /**
     * 买家用户ID(openid/buyer_id等)
     */
    private String buyerId;

    /**
     * 买家账号
     */
    private String buyerAccount;

    /**
     * 回调通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 备注
     */
    private String remark;
}
