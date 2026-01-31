package com.edu.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    /**
     * 短信服务提供商：aliyun-阿里云，tencent-腾讯云
     */
    private String provider = "aliyun";

    /**
     * 阿里云短信配置
     */
    private AliyunConfig aliyun = new AliyunConfig();

    /**
     * 腾讯云短信配置
     */
    private TencentConfig tencent = new TencentConfig();

    /**
     * 是否启用Mock模式（用于测试）
     */
    private Boolean mockEnabled = true;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;

    /**
     * 阿里云配置
     */
    @Data
    public static class AliyunConfig {
        /**
         * AccessKey ID
         */
        private String accessKeyId;

        /**
         * AccessKey Secret
         */
        private String accessKeySecret;

        /**
         * 短信签名
         */
        private String signName = "教育管理系统";

        /**
         * 区域ID
         */
        private String regionId = "cn-hangzhou";

        /**
         * 端点
         */
        private String endpoint = "dysmsapi.aliyuncs.com";
    }

    /**
     * 腾讯云配置
     */
    @Data
    public static class TencentConfig {
        /**
         * Secret ID
         */
        private String secretId;

        /**
         * Secret Key
         */
        private String secretKey;

        /**
         * SDK App ID
         */
        private String sdkAppId;

        /**
         * 短信签名
         */
        private String signName = "教育管理系统";

        /**
         * 区域
         */
        private String region = "ap-guangzhou";
    }
}
