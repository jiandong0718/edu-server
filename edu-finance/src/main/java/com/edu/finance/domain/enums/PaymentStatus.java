package com.edu.finance.domain.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PaymentStatus {

    PENDING("pending", "待支付"),
    PAYING("paying", "支付中"),
    PAID("paid", "已支付"),
    FAILED("failed", "支付失败"),
    CANCELLED("cancelled", "已取消"),
    REFUNDED("refunded", "已退款");

    private final String code;
    private final String name;

    PaymentStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PaymentStatus fromCode(String code) {
        for (PaymentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的支付状态: " + code);
    }
}
