package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.payment.PaymentGateway;
import com.edu.finance.service.OnlinePaymentService;
import com.edu.finance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线支付服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlinePaymentServiceImpl implements OnlinePaymentService {

    private final PaymentService paymentService;
    private final List<PaymentGateway> paymentGateways;

    // 支付网关缓存
    private final Map<String, PaymentGateway> gatewayCache = new ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OnlinePaymentResponse createPayment(OnlinePaymentRequest request) {
        try {
            log.info("创建在线支付订单: contractId={}, studentId={}, amount={}, channel={}",
                    request.getContractId(), request.getStudentId(),
                    request.getAmount(), request.getPaymentChannel());

            // 验证参数
            validatePaymentRequest(request);

            // 创建或获取收款记录
            Payment payment = getOrCreatePayment(request);
            request.setPaymentId(payment.getId());

            // 获取支付网关
            PaymentGateway gateway = getPaymentGateway(request.getPaymentChannel());

            // 创建支付订单
            OnlinePaymentResponse response = gateway.createPayment(request);

            // 更新收款记录状态
            if (StrUtil.isNotBlank(response.getPaymentCredential())) {
                payment.setStatus("paying");
                paymentService.updateById(payment);
            }

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建在线支付订单失败", e);
            throw new BusinessException("创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public PaymentNotification queryPayment(Long paymentId) {
        try {
            Payment payment = paymentService.getById(paymentId);
            if (payment == null) {
                throw new BusinessException("收款记录不存在");
            }

            PaymentGateway gateway = getPaymentGateway(payment.getPaymentMethod());
            return gateway.queryPayment(payment.getPaymentNo());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询支付订单失败: paymentId={}", paymentId, e);
            throw new BusinessException("查询支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelPayment(Long paymentId) {
        try {
            Payment payment = paymentService.getById(paymentId);
            if (payment == null) {
                throw new BusinessException("收款记录不存在");
            }

            if (!"pending".equals(payment.getStatus()) && !"paying".equals(payment.getStatus())) {
                throw new BusinessException("只有待支付或支付中的订单才能取消");
            }

            // 关闭支付订单
            PaymentGateway gateway = getPaymentGateway(payment.getPaymentMethod());
            boolean closed = gateway.closePayment(payment.getPaymentNo());

            if (closed) {
                payment.setStatus("cancelled");
                paymentService.updateById(payment);
            }

            return closed;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消支付订单失败: paymentId={}", paymentId, e);
            throw new BusinessException("取消支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleNotification(String channel, Map<String, String> params) {
        try {
            log.info("处理支付回调通知: channel={}, params={}", channel, params);

            // 获取支付网关
            PaymentGateway gateway = getPaymentGateway(channel);

            // 验证签名
            if (!gateway.verifyNotification(params)) {
                log.error("支付回调签名验证失败: channel={}", channel);
                return gateway.generateNotificationResponse(false);
            }

            // 解析通知
            PaymentNotification notification = gateway.parseNotification(params);
            if (notification == null) {
                log.error("解析支付回调失败: channel={}", channel);
                return gateway.generateNotificationResponse(false);
            }

            // 处理支付结果
            boolean success = processPaymentNotification(notification);

            return gateway.generateNotificationResponse(success);

        } catch (Exception e) {
            log.error("处理支付回调通知失败: channel={}", channel, e);
            PaymentGateway gateway = getPaymentGateway(channel);
            return gateway.generateNotificationResponse(false);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncPaymentStatus(Long paymentId) {
        try {
            PaymentNotification notification = queryPayment(paymentId);
            if (notification == null) {
                return false;
            }

            return processPaymentNotification(notification);

        } catch (Exception e) {
            log.error("同步支付状态失败: paymentId={}", paymentId, e);
            return false;
        }
    }

    /**
     * 验证支付请求
     */
    private void validatePaymentRequest(OnlinePaymentRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }

        if (StrUtil.isBlank(request.getPaymentChannel())) {
            throw new BusinessException("支付渠道不能为空");
        }

        if (request.getStudentId() == null) {
            throw new BusinessException("学员ID不能为空");
        }
    }

    /**
     * 获取或创建收款记录
     */
    private Payment getOrCreatePayment(OnlinePaymentRequest request) {
        if (request.getPaymentId() != null) {
            Payment payment = paymentService.getById(request.getPaymentId());
            if (payment != null) {
                return payment;
            }
        }

        // 创建新的收款记录
        Payment payment = new Payment();
        payment.setContractId(request.getContractId());
        payment.setStudentId(request.getStudentId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentChannel());
        payment.setPaymentScene(request.getPaymentScene());
        payment.setStatus("pending");
        payment.setRemark(request.getRemark());

        paymentService.createPayment(payment);
        return payment;
    }

    /**
     * 获取支付网关
     */
    private PaymentGateway getPaymentGateway(String channel) {
        if (StrUtil.isBlank(channel)) {
            throw new BusinessException("支付渠道不能为空");
        }

        return gatewayCache.computeIfAbsent(channel, key -> {
            for (PaymentGateway gateway : paymentGateways) {
                if (gateway.getChannel().equals(key)) {
                    return gateway;
                }
            }
            throw new BusinessException("不支持的支付渠道: " + key);
        });
    }

    /**
     * 处理支付通知
     */
    private boolean processPaymentNotification(PaymentNotification notification) {
        try {
            // 查找收款记录
            Payment payment = paymentService.lambdaQuery()
                    .eq(Payment::getPaymentNo, notification.getOutTradeNo())
                    .one();

            if (payment == null) {
                log.error("收款记录不存在: paymentNo={}", notification.getOutTradeNo());
                return false;
            }

            // 检查是否已处理
            if ("paid".equals(payment.getStatus()) || "refunded".equals(payment.getStatus())) {
                log.info("收款记录已处理: paymentNo={}, status={}", payment.getPaymentNo(), payment.getStatus());
                return true;
            }

            // 更新支付状态
            if ("success".equals(notification.getStatus())) {
                payment.setStatus("paid");
                payment.setPayTime(notification.getPayTime() != null ? notification.getPayTime() : LocalDateTime.now());
                payment.setTransactionNo(notification.getTransactionNo());
                payment.setBuyerId(notification.getBuyerId());
                payment.setBuyerAccount(notification.getBuyerAccount());
                payment.setNotifyTime(LocalDateTime.now());
                paymentService.updateById(payment);

                // 确认收款(触发后续业务逻辑)
                paymentService.confirmPayment(payment.getId(), notification.getTransactionNo());

                log.info("支付成功: paymentNo={}, transactionNo={}",
                        payment.getPaymentNo(), notification.getTransactionNo());
            } else {
                payment.setStatus("failed");
                payment.setNotifyTime(LocalDateTime.now());
                payment.setErrorCode(notification.getErrorCode());
                payment.setErrorMsg(notification.getErrorMsg());
                paymentService.updateById(payment);

                log.info("支付失败: paymentNo={}, errorMsg={}",
                        payment.getPaymentNo(), notification.getErrorMsg());
            }

            return true;

        } catch (Exception e) {
            log.error("处理支付通知失败: outTradeNo={}", notification.getOutTradeNo(), e);
            return false;
        }
    }
}
