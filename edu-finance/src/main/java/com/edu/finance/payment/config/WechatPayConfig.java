package com.edu.finance.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "payment.wechat")
public class WechatPayConfig {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API v3密钥
     */
    private String apiV3Key;

    /**
     * 商户证书序列号
     */
    private String serialNo;

    /**
     * 商户私钥路径
     */
    private String privateKeyPath;

    /**
     * 支付回调通知URL
     */
    private String notifyUrl;

    /**
     * API域名
     */
    private String apiUrl = "https://api.mch.weixin.qq.com";

    /**
     * 是否启用
     */
    private boolean enabled = false;
}
