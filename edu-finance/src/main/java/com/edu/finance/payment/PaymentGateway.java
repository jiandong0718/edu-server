package com.edu.finance.payment;

import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;

import java.util.Map;

/**
 * 支付网关接口
 * 定义统一的支付接口，由各支付渠道实现
 */
public interface PaymentGateway {

    /**
     * 获取支付渠道代码
     */
    String getChannel();

    /**
     * 创建支付订单
     *
     * @param request 支付请求
     * @return 支付响应
     */
    OnlinePaymentResponse createPayment(OnlinePaymentRequest request);

    /**
     * 查询支付订单状态
     *
     * @param outTradeNo 商户订单号
     * @return 支付通知
     */
    PaymentNotification queryPayment(String outTradeNo);

    /**
     * 关闭支付订单
     *
     * @param outTradeNo 商户订单号
     * @return 是否成功
     */
    boolean closePayment(String outTradeNo);

    /**
     * 解析支付回调通知
     *
     * @param params 回调参数
     * @return 支付通知
     */
    PaymentNotification parseNotification(Map<String, String> params);

    /**
     * 验证回调签名
     *
     * @param params 回调参数
     * @return 是否验证通过
     */
    boolean verifyNotification(Map<String, String> params);

    /**
     * 生成回调响应
     *
     * @param success 是否成功
     * @return 响应内容
     */
    String generateNotificationResponse(boolean success);
}
