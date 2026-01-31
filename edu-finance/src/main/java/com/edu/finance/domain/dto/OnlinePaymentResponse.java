package com.edu.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在线支付响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "在线支付响应")
public class OnlinePaymentResponse {

    @Schema(description = "收款记录ID")
    private Long paymentId;

    @Schema(description = "收款单号")
    private String paymentNo;

    @Schema(description = "支付渠道订单号")
    private String channelOrderNo;

    @Schema(description = "支付渠道")
    private String paymentChannel;

    @Schema(description = "支付场景")
    private String paymentScene;

    @Schema(description = "支付凭证(二维码URL/支付表单/跳转URL等)")
    private String paymentCredential;

    @Schema(description = "支付凭证类型: qrcode-二维码, form-表单, url-跳转链接, jsapi-JSAPI参数")
    private String credentialType;

    @Schema(description = "过期时间(秒)")
    private Integer expireSeconds;

    @Schema(description = "错误信息")
    private String errorMsg;
}
