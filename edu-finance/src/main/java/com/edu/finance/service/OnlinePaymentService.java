package com.edu.finance.service;

import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;

import java.util.Map;

/**
 * 在线支付服务接口
 */
public interface OnlinePaymentService {

    /**
     * 创建在线支付订单
     *
     * @param request 支付请求
     * @return 支付响应
     */
    OnlinePaymentResponse createPayment(OnlinePaymentRequest request);

    /**
     * 查询支付订单状态
     *
     * @param paymentId 收款记录ID
     * @return 支付通知
     */
    PaymentNotification queryPayment(Long paymentId);

    /**
     * 取消支付订单
     *
     * @param paymentId 收款记录ID
     * @return 是否成功
     */
    boolean cancelPayment(Long paymentId);

    /**
     * 处理支付回调通知
     *
     * @param channel 支付渠道
     * @param params 回调参数
     * @return 响应内容
     */
    String handleNotification(String channel, Map<String, String> params);

    /**
     * 同步支付状态
     *
     * @param paymentId 收款记录ID
     * @return 是否成功
     */
    boolean syncPaymentStatus(Long paymentId);
}
