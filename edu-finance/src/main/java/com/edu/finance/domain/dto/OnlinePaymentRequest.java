package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 在线支付请求DTO
 */
@Data
@Schema(description = "在线支付请求")
public class OnlinePaymentRequest {

    @Schema(description = "收款记录ID")
    private Long paymentId;

    @Schema(description = "合同ID")
    private Long contractId;

    @Schema(description = "学员ID")
    private Long studentId;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付渠道: wechat-微信, alipay-支付宝, unionpay-银联")
    private String paymentChannel;

    @Schema(description = "支付场景: app-APP支付, h5-H5支付, native-扫码支付, jsapi-公众号/小程序支付")
    private String paymentScene;

    @Schema(description = "用户标识(微信openid/支付宝buyer_id等)")
    private String userId;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "回调通知URL")
    private String notifyUrl;

    @Schema(description = "前端回跳URL")
    private String returnUrl;

    @Schema(description = "备注")
    private String remark;
}
