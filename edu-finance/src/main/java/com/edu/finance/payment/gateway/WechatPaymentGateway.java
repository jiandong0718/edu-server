package com.edu.finance.payment.gateway;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;
import com.edu.finance.payment.PaymentGateway;
import com.edu.finance.payment.config.WechatPayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微信支付网关实现
 * 注意: 这是一个简化的实现示例，生产环境需要使用官方SDK
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "payment.wechat", name = "enabled", havingValue = "true")
public class WechatPaymentGateway implements PaymentGateway {

    private final WechatPayConfig config;

    @Override
    public String getChannel() {
        return "wechat";
    }

    @Override
    public OnlinePaymentResponse createPayment(OnlinePaymentRequest request) {
        try {
            log.info("创建微信支付订单: paymentNo={}, amount={}, scene=",
                    request.getPaymentId(), request.getAmount(), request.getPaymentScene());

            // 构建请求参数
            Map<String, Object> params = buildPaymentParams(request);

            // 根据支付场景选择不同的API
            String apiPath = getApiPath(request.getPaymentScene());
            String url = config.getApiUrl() + apiPath;

            // 发送请求(实际应使用官方SDK)
            String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
            JSONObject result = JSONUtil.parseObj(response);

            // 解析响应
            return parsePaymentResponse(result, request);

        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            return OnlinePaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .errorMsg("创建支付订单失败: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentNotification queryPayment(String outTradeNo) {
        try {
            log.info("查询微信支付订单: outTradeNo={}", outTradeNo);

            String url = config.getApiUrl() + "/v3/pay/transactions/out-trade-no/" + outTradeNo;
            String response = HttpUtil.get(url);
            JSONObject result = JSONUtil.parseObj(response);

            return parseQueryResponse(result);

        } catch (Exception e) {
            log.error("查询微信支付订单失败", e);
            return null;
        }
    }

    @Override
    public boolean closePayment(String outTradeNo) {
        try {
            log.info("关闭微信支付订单: outTradeNo={}", outTradeNo);

            String url = config.getApiUrl() + "/v3/pay/transactions/out-trade-no/" + outTradeNo + "/close";
            Map<String, Object> params = new HashMap<>();
            params.put("mchid", config.getMchId());

            String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
            return StrUtil.isNotBlank(response);

        } catch (Exception e) {
            log.error("关闭微信支付订单失败", e);
            return false;
        }
    }

    @Override
    public PaymentNotification parseNotification(Map<String, String> params) {
        try {
            // 实际应解密回调数据
            String resource = params.get("resource");
            JSONObject data = JSONUtil.parseObj(resource);

            return PaymentNotification.builder()
                    .paymentChannel("wechat")
                    .outTradeNo(data.getStr("out_trade_no"))
                    .transactionNo(data.getStr("transaction_id"))
                    .amount(new BigDecimal(data.getStr("amount.total")).divide(new BigDecimal("100")))
                    .status("SUCCESS".equals(data.getStr("trade_state")) ? "success" : "failed")
                    .payTime(parseWechatTime(data.getStr("success_time")))
                    .buyerId(data.getStr("payer.openid"))
                    .rawData(JSONUtil.toJsonStr(params))
                    .build();

        } catch (Exception e) {
            log.error("解析微信支付回调失败", e);
            return null;
        }
    }

    @Override
    public boolean verifyNotification(Map<String, String> params) {
        try {
            // 实际应验证签名
            String signature = params.get("signature");
            String timestamp = params.get("timestamp");
            String nonce = params.get("nonce");
            String body = params.get("body");

            // 简化验证逻辑
            return StrUtil.isNotBlank(signature);

        } catch (Exception e) {
            log.error("验证微信支付回调签名失败", e);
            return false;
        }
    }

    @Override
    public String generateNotificationResponse(boolean success) {
        JSONObject response = new JSONObject();
        response.set("code", success ? "SUCCESS" : "FAIL");
        response.set("message", success ? "成功" : "失败");
        return response.toString();
    }

    /**
     * 构建支付参数
     */
    private Map<String, Object> buildPaymentParams(OnlinePaymentRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", config.getAppId());
        params.put("mchid", config.getMchId());
        params.put("description", "学费缴纳");
        params.put("out_trade_no", "SK" + request.getPaymentId());
        params.put("notify_url", StrUtil.isNotBlank(request.getNotifyUrl()) ?
                request.getNotifyUrl() : config.getNotifyUrl());

        // 金额(分)
        Map<String, Object> amount = new HashMap<>();
        amount.put("total", request.getAmount().multiply(new BigDecimal("100")).intValue());
        amount.put("currency", "CNY");
        params.put("amount", amount);

        // 支付者信息
        if (StrUtil.isNotBlank(request.getUserId())) {
            Map<String, Object> payer = new HashMap<>();
            payer.put("openid", request.getUserId());
            params.put("payer", payer);
        }

        return params;
    }

    /**
     * 获取API路径
     */
    private String getApiPath(String scene) {
        return switch (StrUtil.nullToDefault(scene, "native")) {
            case "app" -> "/v3/pay/transactions/app";
            case "h5" -> "/v3/pay/transactions/h5";
            case "jsapi" -> "/v3/pay/transactions/jsapi";
            default -> "/v3/pay/transactions/native";
        };
    }

    /**
     * 解析支付响应
     */
    private OnlinePaymentResponse parsePaymentResponse(JSONObject result, OnlinePaymentRequest request) {
        String scene = StrUtil.nullToDefault(request.getPaymentScene(), "native");

        return OnlinePaymentResponse.builder()
                .paymentId(request.getPaymentId())
                .paymentNo("SK" + request.getPaymentId())
                .paymentChannel("wechat")
                .paymentScene(scene)
                .paymentCredential(extractCredential(result, scene))
                .credentialType(getCredentialType(scene))
                .expireSeconds(7200)
                .build();
    }

    /**
     * 提取支付凭证
     */
    private String extractCredential(JSONObject result, String scene) {
        return switch (scene) {
            case "native" -> result.getStr("code_url");
            case "h5" -> result.getStr("h5_url");
            case "app" -> result.getStr("prepay_id");
            case "jsapi" -> result.getStr("prepay_id");
            default -> result.toString();
        };
    }

    /**
     * 获取凭证类型
     */
    private String getCredentialType(String scene) {
        return switch (scene) {
            case "native" -> "qrcode";
            case "h5" -> "url";
            case "app", "jsapi" -> "jsapi";
            default -> "qrcode";
        };
    }

    /**
     * 解析查询响应
     */
    private PaymentNotification parseQueryResponse(JSONObject result) {
        return PaymentNotification.builder()
                .paymentChannel("wechat")
                .outTradeNo(result.getStr("out_trade_no"))
                .transactionNo(result.getStr("transaction_id"))
                .amount(new BigDecimal(result.getStr("amount.total")).divide(new BigDecimal("100")))
                .status("SUCCESS".equals(result.getStr("trade_state")) ? "success" : "failed")
                .payTime(parseWechatTime(result.getStr("success_time")))
                .buyerId(result.getStr("payer.openid"))
                .build();
    }

    /**
     * 解析微信时间格式
     */
    private LocalDateTime parseWechatTime(String timeStr) {
        if (StrUtil.isBlank(timeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (Exception e) {
            log.warn("解析微信时间失败: {}", timeStr);
            return null;
        }
    }
}
