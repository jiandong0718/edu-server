package com.edu.finance.domain.enums;

import lombok.Getter;

/**
 * 支付渠道枚举
 */
@Getter
public enum PaymentChannel {

    WECHAT("wechat", "微信支付"),
    ALIPAY("alipay", "支付宝支付"),
    UNIONPAY("unionpay", "银联支付"),
    CASH("cash", "现金支付"),
    POS("pos", "POS机支付");

    private final String code;
    private final String name;

    PaymentChannel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PaymentChannel fromCode(String code) {
        for (PaymentChannel channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("未知的支付渠道: " + code);
    }
}
