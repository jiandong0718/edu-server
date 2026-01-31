package com.edu.notification.service.sms.impl;

import com.edu.notification.config.SmsProperties;
import com.edu.notification.service.sms.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 阿里云短信发送器实现
 *
 * 注意：这是Mock实现，用于演示和测试
 * 真实环境需要集成阿里云SMS SDK：
 * 1. 添加Maven依赖：com.aliyun:dysmsapi20170525
 * 2. 使用阿里云SDK的Client进行真实发送
 * 3. 处理真实的响应和异常
 */
@Slf4j
@Component("aliyunSmsSender")
public class AliyunSmsSenderImpl implements SmsSender {

    private final SmsProperties smsProperties;

    public AliyunSmsSenderImpl(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public SmsResult sendSms(String phone, String content) {
        log.info("阿里云短信发送 - 手机号: {}, 内容: {}", phone, content);

        if (smsProperties.getMockEnabled()) {
            return mockSend(phone, content);
        }

        // TODO: 真实环境集成阿里云SMS SDK
        // 示例代码：
        // try {
        //     Config config = new Config()
        //         .setAccessKeyId(smsProperties.getAliyun().getAccessKeyId())
        //         .setAccessKeySecret(smsProperties.getAliyun().getAccessKeySecret());
        //     config.endpoint = smsProperties.getAliyun().getEndpoint();
        //
        //     Client client = new Client(config);
        //     SendSmsRequest request = new SendSmsRequest()
        //         .setPhoneNumbers(phone)
        //         .setSignName(smsProperties.getAliyun().getSignName())
        //         .setTemplateCode(templateCode)
        //         .setTemplateParam(JSON.toJSONString(params));
        //
        //     SendSmsResponse response = client.sendSms(request);
        //     if ("OK".equals(response.getBody().getCode())) {
        //         return SmsResult.success(response.getBody().getBizId(), "0.05");
        //     } else {
        //         return SmsResult.fail(response.getBody().getMessage());
        //     }
        // } catch (Exception e) {
        //     log.error("阿里云短信发送失败", e);
        //     return SmsResult.fail(e.getMessage());
        // }

        return mockSend(phone, content);
    }

    @Override
    public List<SmsResult> sendBatchSms(List<String> phones, String content) {
        log.info("阿里云批量短信发送 - 手机号数量: {}, 内容: {}", phones.size(), content);

        List<SmsResult> results = new ArrayList<>();
        for (String phone : phones) {
            results.add(sendSms(phone, content));
        }
        return results;
    }

    @Override
    public SmsResult sendTemplateSms(String phone, String templateCode, Map<String, String> params) {
        log.info("阿里云模板短信发送 - 手机号: {}, 模板: {}, 参数: {}", phone, templateCode, params);

        if (smsProperties.getMockEnabled()) {
            String content = buildContentFromTemplate(templateCode, params);
            return mockSend(phone, content);
        }

        // TODO: 真实环境集成阿里云SMS SDK
        return mockSend(phone, "模板短信内容");
    }

    @Override
    public List<SmsResult> sendBatchTemplateSms(List<String> phones, String templateCode, Map<String, String> params) {
        log.info("阿里云批量模板短信发送 - 手机号数量: {}, 模板: {}, 参数: {}", phones.size(), templateCode, params);

        List<SmsResult> results = new ArrayList<>();
        for (String phone : phones) {
            results.add(sendTemplateSms(phone, templateCode, params));
        }
        return results;
    }

    @Override
    public String querySendStatus(String thirdPartyId) {
        log.info("阿里云查询短信发送状态 - 消息ID: {}", thirdPartyId);

        if (smsProperties.getMockEnabled()) {
            return "success";
        }

        // TODO: 真实环境调用阿里云查询接口
        return "success";
    }

    @Override
    public String getProvider() {
        return "aliyun";
    }

    /**
     * Mock发送（用于测试）
     */
    private SmsResult mockSend(String phone, String content) {
        // 模拟发送成功
        String thirdPartyId = "ALIYUN-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Mock阿里云短信发送成功 - 手机号: {}, 消息ID: {}", phone, thirdPartyId);
        return SmsResult.success(thirdPartyId, "0.05");
    }

    /**
     * 从模板构建内容
     */
    private String buildContentFromTemplate(String templateCode, Map<String, String> params) {
        // 简单的模板替换逻辑
        String content = "【" + smsProperties.getAliyun().getSignName() + "】";

        if ("VERIFY_CODE".equals(templateCode)) {
            content += "您的验证码是：" + params.getOrDefault("code", "123456") +
                      "，" + params.getOrDefault("time", "5") + "分钟内有效。";
        } else if ("NOTIFY".equals(templateCode)) {
            content += params.getOrDefault("content", "您有新的通知，请及时查看。");
        } else {
            content += "通知内容";
        }

        return content;
    }
}
