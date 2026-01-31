package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付回调通知DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "支付回调通知")
public class PaymentNotification {

    @Schema(description = "支付渠道")
    private String paymentChannel;

    @Schema(description = "商户订单号(收款单号)")
    private String outTradeNo;

    @Schema(description = "支付渠道交易号")
    private String transactionNo;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付状态: success-成功, failed-失败")
    private String status;

    @Schema(description = "支付完成时间")
    private LocalDateTime payTime;

    @Schema(description = "买家账号")
    private String buyerAccount;

    @Schema(description = "买家用户ID")
    private String buyerId;

    @Schema(description = "原始通知数据")
    private String rawData;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;
}
