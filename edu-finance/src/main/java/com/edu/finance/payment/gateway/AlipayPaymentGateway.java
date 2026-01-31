package com.edu.finance.payment.gateway;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;
import com.edu.finance.payment.PaymentGateway;
import com.edu.finance.payment.config.AlipayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 支付宝支付网关实现
 * 注意: 这是一个简化的实现示例，生产环境需要使用官方SDK
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "payment.alipay", name = "enabled", havingValue = "true")
public class AlipayPaymentGateway implements PaymentGateway {

    private final AlipayConfig config;

    @Override
    public String getChannel() {
        return "alipay";
    }

    @Override
    public OnlinePaymentResponse createPayment(OnlinePaymentRequest request) {
        try {
            log.info("创建支付宝支付订单: paymentNo={}, amount={}, scene={}",
                    request.getPaymentId(), request.getAmount(), request.getPaymentScene());

            // 构建请求参数
            Map<String, String> params = buildPaymentParams(request);

            // 生成签名
            String sign = generateSign(params);
            params.put("sign", sign);

            // 根据支付场景选择不同的处理方式
            String credential = generateCredential(params, request.getPaymentScene());

            return OnlinePaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .paymentNo("SK" + request.getPaymentId())
                    .paymentChannel("alipay")
                    .paymentScene(request.getPaymentScene())
                    .paymentCredential(credential)
                    .credentialType(getCredentialType(request.getPaymentScene()))
                    .expireSeconds(1800)
                    .build();

        } catch (Exception e) {
            log.error("创建支付宝支付订单失败", e);
            return OnlinePaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .errorMsg("创建支付订单失败: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentNotification queryPayment(String outTradeNo) {
        try {
            log.info("查询支付宝支付订单: outTradeNo={}", outTradeNo);

            Map<String, String> params = new HashMap<>();
            params.put("app_id", config.getAppId());
            params.put("method", "alipay.trade.query");
            params.put("charset", config.getCharset());
            params.put("sign_type", config.getSignType());
            params.put("timestamp", DateUtil.now());
            params.put("version", "1.0");

            JSONObject bizContent = new JSONObject();
            bizContent.set("out_trade_no", outTradeNo);
            params.put("biz_content", bizContent.toString());

            // 生成签名
            String sign = generateSign(params);
            params.put("sign", sign);

            // 发送请求
            String response = HttpUtil.post(config.getGatewayUrl(), params);
            JSONObject result = JSONUtil.parseObj(response);

            return parseQueryResponse(result);

        } catch (Exception e) {
            log.error("查询支付宝支付订单失败", e);
            return null;
        }
    }

    @Override
    public boolean closePayment(String outTradeNo) {
        try {
            log.info("关闭支付宝支付订单: outTradeNo={}", outTradeNo);

            Map<String, String> params = new HashMap<>();
            params.put("app_id", config.getAppId());
            params.put("method", "alipay.trade.close");
            params.put("charset", config.getCharset());
            params.put("sign_type", config.getSignType());
            params.put("timestamp", DateUtil.now());
            params.put("version", "1.0");

            JSONObject bizContent = new JSONObject();
            bizContent.set("out_trade_no", outTradeNo);
            params.put("biz_content", bizContent.toString());

            // 生成签名
            String sign = generateSign(params);
            params.put("sign", sign);

            // 发送请求
            String response = HttpUtil.post(config.getGatewayUrl(), params);
            JSONObject result = JSONUtil.parseObj(response);

            return result.getJSONObject("alipay_trade_close_response")
                    .getStr("code").equals("10000");

        } catch (Exception e) {
            log.error("关闭支付宝支付订单失败", e);
            return false;
        }
    }

    @Override
    public PaymentNotification parseNotification(Map<String, String> params) {
        try {
            String tradeStatus = params.get("trade_status");
            boolean success = "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);

            return PaymentNotification.builder()
                    .paymentChannel("alipay")
                    .outTradeNo(params.get("out_trade_no"))
                    .transactionNo(params.get("trade_no"))
                    .amount(new BigDecimal(params.get("total_amount")))
                    .status(success ? "success" : "failed")
                    .payTime(parseAlipayTime(params.get("gmt_payment")))
                    .buyerAccount(params.get("buyer_logon_id"))
                    .buyerId(params.get("buyer_id"))
                    .rawData(JSONUtil.toJsonStr(params))
                    .build();

        } catch (Exception e) {
            log.error("解析支付宝支付回调失败", e);
            return null;
        }
    }

    @Override
    public boolean verifyNotification(Map<String, String> params) {
        try {
            String sign = params.get("sign");
            String signType = params.get("sign_type");

            // 移除sign和sign_type参数
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("sign");
            verifyParams.remove("sign_type");

            // 生成待验签字符串
            String content = verifyParams.entrySet().stream()
                    .filter(e -> StrUtil.isNotBlank(e.getValue()))
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            // 实际应使用支付宝公钥验签
            log.info("验证支付宝回调签名: content={}", content);
            return StrUtil.isNotBlank(sign);

        } catch (Exception e) {
            log.error("验证支付宝支付回调签名失败", e);
            return false;
        }
    }

    @Override
    public String generateNotificationResponse(boolean success) {
        return success ? "success" : "failure";
    }

    /**
     * 构建支付参数
     */
    private Map<String, String> buildPaymentParams(OnlinePaymentRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("app_id", config.getAppId());
        params.put("method", getPayMethod(request.getPaymentScene()));
        params.put("charset", config.getCharset());
        params.put("sign_type", config.getSignType());
        params.put("timestamp", DateUtil.now());
        params.put("version", "1.0");
        params.put("notify_url", StrUtil.isNotBlank(request.getNotifyUrl()) ?
                request.getNotifyUrl() : config.getNotifyUrl());

        if (StrUtil.isNotBlank(request.getReturnUrl())) {
            params.put("return_url", request.getReturnUrl());
        }

        // 业务参数
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", "SK" + request.getPaymentId());
        bizContent.set("total_amount", request.getAmount().toString());
        bizContent.set("subject", "学费缴纳");
        bizContent.set("product_code", getProductCode(request.getPaymentScene()));

        params.put("biz_content", bizContent.toString());

        return params;
    }

    /**
     * 获取支付方法
     */
    private String getPayMethod(String scene) {
        return switch (StrUtil.nullToDefault(scene, "page")) {
            case "app" -> "alipay.trade.app.pay";
            case "wap" -> "alipay.trade.wap.pay";
            case "native" -> "alipay.trade.precreate";
            default -> "alipay.trade.page.pay";
        };
    }

    /**
     * 获取产品代码
     */
    private String getProductCode(String scene) {
        return switch (StrUtil.nullToDefault(scene, "page")) {
            case "app" -> "QUICK_MSECURITY_PAY";
            case "wap" -> "QUICK_WAP_WAY";
            case "native" -> "FACE_TO_FACE_PAYMENT";
            default -> "FAST_INSTANT_TRADE_PAY";
        };
    }

    /**
     * 生成签名
     */
    private String generateSign(Map<String, String> params) {
        // 排序并拼接参数
        String content = new TreeMap<>(params).entrySet().stream()
                .filter(e -> StrUtil.isNotBlank(e.getValue()) && !"sign".equals(e.getKey()))
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // 实际应使用RSA签名
        return DigestUtil.md5Hex(content + config.getPrivateKey());
    }

    /**
     * 生成支付凭证
     */
    private String generateCredential(Map<String, String> params, String scene) {
        String queryString = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        if ("native".equals(scene)) {
            // 扫码支付返回二维码内容
            return queryString;
        } else {
            // 其他方式返回完整URL
            return config.getGatewayUrl() + "?" + queryString;
        }
    }

    /**
     * 获取凭证类型
     */
    private String getCredentialType(String scene) {
        return "native".equals(scene) ? "qrcode" : "url";
    }

    /**
     * 解析查询响应
     */
    private PaymentNotification parseQueryResponse(JSONObject result) {
        JSONObject response = result.getJSONObject("alipay_trade_query_response");
        if (response == null || !"10000".equals(response.getStr("code"))) {
            return null;
        }

        String tradeStatus = response.getStr("trade_status");
        boolean success = "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);

        return PaymentNotification.builder()
                .paymentChannel("alipay")
                .outTradeNo(response.getStr("out_trade_no"))
                .transactionNo(response.getStr("trade_no"))
                .amount(new BigDecimal(response.getStr("total_amount")))
                .status(success ? "success" : "failed")
                .payTime(parseAlipayTime(response.getStr("send_pay_date")))
                .buyerId(response.getStr("buyer_user_id"))
                .build();
    }

    /**
     * 解析支付宝时间格式
     */
    private LocalDateTime parseAlipayTime(String timeStr) {
        if (StrUtil.isBlank(timeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析支付宝时间失败: {}", timeStr);
            return null;
        }
    }
}
