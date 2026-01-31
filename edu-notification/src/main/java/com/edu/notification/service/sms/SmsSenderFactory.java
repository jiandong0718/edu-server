package com.edu.notification.service.sms;

import com.edu.notification.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信发送工厂
 * 根据配置选择不同的短信服务提供商
 */
@Slf4j
@Component
public class SmsSenderFactory {

    private final SmsProperties smsProperties;
    private final Map<String, SmsSender> smsSenderMap;

    @Autowired
    public SmsSenderFactory(SmsProperties smsProperties, Map<String, SmsSender> smsSenderMap) {
        this.smsProperties = smsProperties;
        this.smsSenderMap = smsSenderMap;
        log.info("短信发送工厂初始化 - 当前提供商: , 可用提供商: {}",
                smsProperties.getProvider(), smsSenderMap.keySet());
    }

    /**
     * 获取短信发送器
     *
     * @return 短信发送器实例
     */
    public SmsSender getSmsSender() {
        String provider = smsProperties.getProvider();
        SmsSender sender = null;

        // 根据配置选择发送器
        if ("aliyun".equalsIgnoreCase(provider)) {
            sender = smsSenderMap.get("aliyunSmsSender");
        } else if ("tencent".equalsIgnoreCase(provider)) {
            sender = smsSenderMap.get("tencentSmsSender");
        }

        if (sender == null) {
            log.warn("未找到短信发送器: {}, 使用默认阿里云发送器", provider);
            sender = smsSenderMap.get("aliyunSmsSender");
        }

        if (sender == null) {
            throw new IllegalStateException("无可用的短信发送器");
        }

        log.debug("使用短信发送器: {}", sender.getProvider());
        return sender;
    }

    /**
     * 获取指定提供商的短信发送器
     *
     * @param provider 提供商名称
     * @return 短信发送器实例
     */
    public SmsSender getSmsSender(String provider) {
        SmsSender sender = null;

        if ("aliyun".equalsIgnoreCase(provider)) {
            sender = smsSenderMap.get("aliyunSmsSender");
        } else if ("tencent".equalsIgnoreCase(provider)) {
            sender = smsSenderMap.get("tencentSmsSender");
        }

        if (sender == null) {
            throw new IllegalArgumentException("不支持的短信提供商: " + provider);
        }

        return sender;
    }
}
