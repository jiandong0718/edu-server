package com.edu.notification.service.sms;

import java.util.List;
import java.util.Map;

/**
 * 短信发送器接口
 * 定义统一的短信发送规范，由不同的服务提供商实现
 */
public interface SmsSender {

    /**
     * 发送单条短信
     *
     * @param phone   手机号
     * @param content 短信内容
     * @return 发送结果，包含第三方消息ID
     */
    SmsResult sendSms(String phone, String content);

    /**
     * 批量发送短信
     *
     * @param phones  手机号列表
     * @param content 短信内容
     * @return 批量发送结果
     */
    List<SmsResult> sendBatchSms(List<String> phones, String content);

    /**
     * 发送模板短信
     *
     * @param phone        手机号
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 发送结果
     */
    SmsResult sendTemplateSms(String phone, String templateCode, Map<String, String> params);

    /**
     * 批量发送模板短信
     *
     * @param phones       手机号列表
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 批量发送结果
     */
    List<SmsResult> sendBatchTemplateSms(List<String> phones, String templateCode, Map<String, String> params);

    /**
     * 查询发送状态
     *
     * @param thirdPartyId 第三方消息ID
     * @return 发送状态
     */
    String querySendStatus(String thirdPartyId);

    /**
     * 获取服务提供商名称
     *
     * @return 提供商名称
     */
    String getProvider();

    /**
     * 短信发送结果
     */
    class SmsResult {
        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 第三方消息ID
         */
        private String thirdPartyId;

        /**
         * 失败原因
         */
        private String failReason;

        /**
         * 发送成本（元）
         */
        private String cost;

        public SmsResult() {
        }

        public SmsResult(boolean success, String thirdPartyId, String failReason, String cost) {
            this.success = success;
            this.thirdPartyId = thirdPartyId;
            this.failReason = failReason;
            this.cost = cost;
        }

        public static SmsResult success(String thirdPartyId, String cost) {
            return new SmsResult(true, thirdPartyId, null, cost);
        }

        public static SmsResult fail(String failReason) {
            return new SmsResult(false, null, failReason, null);
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getThirdPartyId() {
            return thirdPartyId;
        }

        public void setThirdPartyId(String thirdPartyId) {
            this.thirdPartyId = thirdPartyId;
        }

        public String getFailReason() {
            return failReason;
        }

        public void setFailReason(String failReason) {
            this.failReason = failReason;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }
}
