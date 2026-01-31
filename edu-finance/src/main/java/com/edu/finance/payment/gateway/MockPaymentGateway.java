package com.edu.finance.payment.gateway;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;
import com.edu.finance.payment.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟支付网关实现
 * 用于开发和测试环境，模拟真实支付流程
 *
 * 使用方式：
 * 1. 在配置文件中设置 payment.mock.enabled=true
 * 2. 创建支付订单时使用 paymentChannel="mock"
 * 3. 调用 /finance/online-payment/notify/mock?outTradeNo=xxx&status=success 模拟支付回调
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "payment.mock", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MockPaymentGateway implements PaymentGateway {

    // 模拟支付订单存储
    private final Map<String, MockPaymentOrder> mockOrders = new ConcurrentHashMap<>();

    @Override
    public String getChannel() {
        return "mock";
    }

    @Override
    public OnlinePaymentResponse createPayment(OnlinePaymentRequest request) {
        try {
            log.info("创建模拟支付订单: paymentId={}, amount={}, scene={}",
                    request.getPaymentId(), request.getAmount(), request.getPaymentScene());

            String outTradeNo = "SK" + request.getPaymentId();
            String transactionNo = "MOCK" + IdUtil.getSnowflakeNextIdStr();

            // 保存模拟订单
            MockPaymentOrder order = new MockPaymentOrder();
            order.setOutTradeNo(outTradeNo);
            order.setTransactionNo(transactionNo);
            order.setAmount(request.getAmount());
            order.setStatus("pending");
            order.setCreateTime(LocalDateTime.now());
            mockOrders.put(outTradeNo, order);

            // 生成模拟支付凭证
            String credential = generateMockCredential(request, outTradeNo);

            return OnlinePaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .paymentNo(outTradeNo)
                    .channelOrderNo(transactionNo)
                    .paymentChannel("mock")
                    .paymentScene(request.getPaymentScene())
                    .paymentCredential(credential)
                    .credentialType(getCredentialType(request.getPaymentScene()))
                    .expireSeconds(1800)
                    .build();

        } catch (Exception e) {
            log.error("创建模拟支付订单失败", e);
            return OnlinePaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .errorMsg("创建支付订单失败: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentNotification queryPayment(String outTradeNo) {
        try {
            log.info("查询模拟支付订单: outTradeNo={}", outTradeNo);

            MockPaymentOrder order = mockOrders.get(outTradeNo);
            if (order == null) {
                log.warn("模拟支付订单不存在: outTradeNo={}", outTradeNo);
                return null;
            }

            return PaymentNotification.builder()
                    .paymentChannel("mock")
                    .outTradeNo(order.getOutTradeNo())
                    .transactionNo(order.getTransactionNo())
                    .amount(order.getAmount())
                    .status(order.getStatus())
                    .payTime(order.getPayTime())
                    .buyerId("mock_buyer_001")
                    .buyerAccount("mock@example.com")
                    .build();

        } catch (Exception e) {
            log.error("查询模拟支付订单失败", e);
            return null;
        }
    }

    @Override
    public boolean closePayment(String outTradeNo) {
        try {
            log.info("关闭模拟支付订单: outTradeNo={}", outTradeNo);

            MockPaymentOrder order = mockOrders.get(outTradeNo);
            if (order != null && "pending".equals(order.getStatus())) {
                order.setStatus("closed");
                return true;
            }

            return false;

        } catch (Exception e) {
            log.error("关闭模拟支付订单失败", e);
            return false;
        }
    }

    @Override
    public PaymentNotification parseNotification(Map<String, String> params) {
        try {
            String outTradeNo = params.get("outTradeNo");
            String status = params.getOrDefault("status", "success");

            MockPaymentOrder order = mockOrders.get(outTradeNo);
            if (order == null) {
                log.error("模拟支付订单不存在: outTradeNo={}", outTradeNo);
                return null;
            }

            // 更新订单状态
            order.setStatus(status);
            order.setPayTime(LocalDateTime.now());

            return PaymentNotification.builder()
                    .paymentChannel("mock")
                    .outTradeNo(order.getOutTradeNo())
                    .transactionNo(order.getTransactionNo())
                    .amount(order.getAmount())
                    .status(status)
                    .payTime(order.getPayTime())
                    .buyerId("mock_buyer_001")
                    .buyerAccount("mock@example.com")
                    .rawData(JSONUtil.toJsonStr(params))
                    .build();

        } catch (Exception e) {
            log.error("解析模拟支付回调失败", e);
            return null;
        }
    }

    @Override
    public boolean verifyNotification(Map<String, String> params) {
        try {
            String outTradeNo = params.get("outTradeNo");

            // 模拟签名验证
            if (StrUtil.isBlank(outTradeNo)) {
                log.error("模拟支付回调参数缺失: outTradeNo");
                return false;
            }

            MockPaymentOrder order = mockOrders.get(outTradeNo);
            if (order == null) {
                log.error("模拟支付订单不存在: outTradeNo={}", outTradeNo);
                return false;
            }

            log.info("模拟支付回调签名验证通过: outTradeNo={}", outTradeNo);
            return true;

        } catch (Exception e) {
            log.error("验证模拟支付回调签名失败", e);
            return false;
        }
    }

    @Override
    public String generateNotificationResponse(boolean success) {
        return success ? "SUCCESS" : "FAIL";
    }

    /**
     * 生成模拟支付凭证
     */
    private String generateMockCredential(OnlinePaymentRequest request, String outTradeNo) {
        String scene = StrUtil.nullToDefault(request.getPaymentScene(), "native");

        return switch (scene) {
            case "native", "qrcode" ->
                // 二维码内容：包含订单号和金额
                String.format("mock://pay?outTradeNo=%s&amount=%s", outTradeNo, request.getAmount());
            case "h5", "wap", "page" ->
                // 跳转URL
                String.format("http://mock-payment.example.com/pay?outTradeNo=%s&amount=%s",
                    outTradeNo, request.getAmount());
            case "app" ->
                // APP支付参数
                String.format("{\"outTradeNo\":\"%s\",\"amount\":\"%s\"}", outTradeNo, request.getAmount());
            case "jsapi" ->
                // JSAPI支付参数
                String.format("prepay_id_mock_%s", IdUtil.getSnowflakeNextIdStr());
            default -> outTradeNo;
        };
    }

    /**
     * 获取凭证类型
     */
    private String getCredentialType(String scene) {
        String sceneStr = StrUtil.nullToDefault(scene, "native");

        return switch (sceneStr) {
            case "native", "qrcode" -> "qrcode";
            case "h5", "wap", "page" -> "url";
            case "app", "jsapi" -> "jsapi";
            default -> "qrcode";
        };
    }

    /**
     * 模拟支付订单
     */
    private static class MockPaymentOrder {
        private String outTradeNo;
        private String transactionNo;
        private BigDecimal amount;
        private String status;
        private LocalDateTime createTime;
        private LocalDateTime payTime;

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public void setOutTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
        }

        public String getTransactionNo() {
            return transactionNo;
        }

        public void setTransactionNo(String transactionNo) {
            this.transactionNo = transactionNo;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public LocalDateTime getPayTime() {
            return payTime;
        }

        public void setPayTime(LocalDateTime payTime) {
            this.payTime = payTime;
        }
    }
}
