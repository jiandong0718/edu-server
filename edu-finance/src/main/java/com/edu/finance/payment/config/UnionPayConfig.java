package com.edu.finance.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 银联支付配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "payment.unionpay")
public class UnionPayConfig {

    /**
     * 商户号
     */
    private String merId;

    /**
     * 证书路径
     */
    private String certPath;

    /**
     * 证书密码
     */
    private String certPassword;

    /**
     * 签名证书序列号
     */
    private String certId;

    /**
     * 验签公钥证书路径
     */
    private String validateCertPath;

    /**
     * 中级证书路径
     */
    private String middleCertPath;

    /**
     * 根证书路径
     */
    private String rootCertPath;

    /**
     * 支付回调通知URL
     */
    private String notifyUrl;

    /**
     * 前端回跳URL
     */
    private String returnUrl;

    /**
     * 前台请求地址
     */
    private String frontUrl = "https://gateway.95516.com/gateway/api/frontTransReq.do";

    /**
     * 后台请求地址
     */
    private String backUrl = "https://gateway.95516.com/gateway/api/backTransReq.do";

    /**
     * 单笔查询地址
     */
    private String singleQueryUrl = "https://gateway.95516.com/gateway/api/queryTrans.do";

    /**
     * 是否启用
     */
    private boolean enabled = false;
}
