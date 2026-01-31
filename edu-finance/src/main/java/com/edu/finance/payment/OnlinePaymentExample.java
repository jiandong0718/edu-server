package com.edu.finance.payment;

import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;
import com.edu.finance.service.OnlinePaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 在线支付使用示例
 *
 * 本类展示了如何使用在线支付功能的各种场景
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OnlinePaymentExample {

    private final OnlinePaymentService onlinePaymentService;

    /**
     * 示例1: 创建微信扫码支付
     */
    public void createWechatNativePayment() {
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("wechat");
        request.setPaymentScene("native");
        request.setClientIp("192.168.1.1");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() != null) {
            log.info("微信扫码支付创建成功");
            log.info("收款单号: {}", response.getPaymentNo());
            log.info("二维码URL: {}", response.getPaymentCredential());
            log.info("过期时间: {}秒", response.getExpireSeconds());

            // 前端可以使用二维码URL生成二维码图片供用户扫描
            // 例如: QRCode.from(response.getPaymentCredential()).to(ImageType.PNG).stream()
        } else {
            log.error("微信扫码支付创建失败: {}", response.getErrorMsg());
        }
    }

    /**
     * 示例2: 创建微信H5支付
     */
    public void createWechatH5Payment() {
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("wechat");
        request.setPaymentScene("h5");
        request.setClientIp("192.168.1.1");
        request.setReturnUrl("https://your-domain.com/payment/return");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() != null) {
            log.info("微信H5支付创建成功");
            log.info("支付URL: {}", response.getPaymentCredential());

            // 前端跳转到支付URL
            // window.location.href = response.getPaymentCredential();
        } else {
            log.error("微信H5支付创建失败: {}", response.getErrorMsg());
        }
    }

    /**
     * 示例3: 创建微信JSAPI支付(公众号/小程序)
     */
    public void createWechatJsapiPayment() {
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("wechat");
        request.setPaymentScene("jsapi");
        request.setUserId("openid_xxx"); // 用户的openid
        request.setClientIp("192.168.1.1");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() != null) {
            log.info("微信JSAPI支付创建成功");
            log.info("prepay_id: {}", response.getPaymentCredential());

            // 前端使用prepay_id调用微信支付JSAPI
            // wx.chooseWXPay({...})
        } else {
            log.error("微信JSAPI支付创建失败: {}", response.getErrorMsg());
        }
    }

    /**
     * 示例4: 创建支付宝网页支付
     */
    public void createAlipayPagePayment() {
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("alipay");
        request.setPaymentScene("page");
        request.setReturnUrl("https://your-domain.com/payment/return");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() != null) {
            log.info("支付宝网页支付创建成功");
            log.info("支付URL: {}", response.getPaymentCredential());

            // 前端跳转到支付宝支付页面
            // window.location.href = response.getPaymentCredential();
        } else {
            log.error("支付宝网页支付创建失败: {}", response.getErrorMsg());
        }
    }

    /**
     * 示例5: 创建支付宝手机网站支付
     */
    public void createAlipayWapPayment() {
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("alipay");
        request.setPaymentScene("wap");
        request.setReturnUrl("https://your-domain.com/payment/return");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() != null) {
            log.info("支付宝手机网站支付创建成功");
            log.info("支付URL: {}", response.getPaymentCredential());
        } else {
            log.error("支付宝手机网站支付创建失败: {}", response.getErrorMsg());
        }
    }

    /**
     * 示例6: 创建支付宝扫码支付
     */
    public void createAlipayNativePayment() {
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("alipay");
        request.setPaymentScene("native");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() != null) {
            log.info("支付宝扫码支付创建成功");
            log.info("二维码内容: {}", response.getPaymentCredential());
        } else {
            log.error("支付宝扫码支付创建失败: {}", response.getErrorMsg());
        }
    }

    /**
     * 示例7: 创建银联支付
     */
    public void createUnionPayPayment() {
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("unionpay");
        request.setPaymentScene("pc");
        request.setReturnUrl("https://your-domain.com/payment/return");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() != null) {
            log.info("银联支付创建成功");
            log.info("支付凭证: {}", response.getPaymentCredential());
        } else {
            log.error("银联支付创建失败: {}", response.getErrorMsg());
        }
    }

    /**
     * 示例8: 查询支付状态
     */
    public void queryPaymentStatus(Long paymentId) {
        PaymentNotification notification = onlinePaymentService.queryPayment(paymentId);

        if (notification != null) {
            log.info("支付状态查询成功");
            log.info("商户订单号: {}", notification.getOutTradeNo());
            log.info("支付渠道交易号: {}", notification.getTransactionNo());
            log.info("支付金额: {}", notification.getAmount());
            log.info("支付状态: {}", notification.getStatus());
            log.info("支付时间: {}", notification.getPayTime());

            if ("success".equals(notification.getStatus())) {
                log.info("支付成功");
            } else if ("failed".equals(notification.getStatus())) {
                log.info("支付失败: {}", notification.getErrorMsg());
            } else {
                log.info("支付进行中");
            }
        } else {
            log.error("支付状态查询失败");
        }
    }

    /**
     * 示例9: 取消支付订单
     */
    public void cancelPayment(Long paymentId) {
        boolean result = onlinePaymentService.cancelPayment(paymentId);

        if (result) {
            log.info("支付订单取消成功");
        } else {
            log.error("支付订单取消失败");
        }
    }

    /**
     * 示例10: 同步支付状态
     *
     * 当回调通知未收到或处理失败时，可以主动同步支付状态
     */
    public void syncPaymentStatus(Long paymentId) {
        boolean result = onlinePaymentService.syncPaymentStatus(paymentId);

        if (result) {
            log.info("支付状态同步成功");
        } else {
            log.error("支付状态同步失败");
        }
    }

    /**
     * 示例11: 完整的支付流程
     */
    public void completePaymentFlow() {
        // 1. 创建支付订单
        OnlinePaymentRequest request = new OnlinePaymentRequest();
        request.setContractId(1L);
        request.setStudentId(100L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setPaymentChannel("wechat");
        request.setPaymentScene("native");
        request.setRemark("学费缴纳");

        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);

        if (response.getPaymentCredential() == null) {
            log.error("创建支付订单失败: {}", response.getErrorMsg());
            return;
        }

        Long paymentId = response.getPaymentId();
        log.info("支付订单创建成功，收款ID: {}", paymentId);
        log.info("请扫描二维码完成支付: {}", response.getPaymentCredential());

        // 2. 前端展示二维码，用户扫码支付

        // 3. 等待支付回调通知（自动处理）
        // 支付平台会调用 /finance/online-payment/notify/wechat 接口

        // 4. 如果长时间未收到回调，可以主动查询支付状态
        try {
            Thread.sleep(5000); // 等待5秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        PaymentNotification notification = onlinePaymentService.queryPayment(paymentId);
        if (notification != null && "success".equals(notification.getStatus())) {
            log.info("支付成功，交易号: {}", notification.getTransactionNo());

            // 5. 支付成功后的业务处理已在回调中自动完成
            // - 更新收款记录状态
            // - 更新合同已收金额
            // - 发布合同支付完成事件
            // - 触发后续业务流程（课时账户充值、发送通知等）
        } else {
            log.info("支付尚未完成");

            // 6. 如果用户取消支付，可以关闭订单
            // onlinePaymentService.cancelPayment(paymentId);
        }
    }

    /**
     * 示例12: 处理支付超时
     *
     * 定时任务扫描超时未支付的订单
     */
    public void handlePaymentTimeout() {
        // 查询所有超时未支付的订单
        // List<Payment> timeoutPayments = paymentService.findTimeoutPayments();

        // 对每个超时订单进行处理
        // for (Payment payment : timeoutPayments) {
        //     // 查询支付状态
        //     PaymentNotification notification = onlinePaymentService.queryPayment(payment.getId());
        //
        //     if (notification != null && "success".equals(notification.getStatus())) {
        //         // 支付成功但回调未收到，同步状态
        //         onlinePaymentService.syncPaymentStatus(payment.getId());
        //     } else {
        //         // 支付未完成，关闭订单
        //         onlinePaymentService.cancelPayment(payment.getId());
        //     }
        // }
    }
}
