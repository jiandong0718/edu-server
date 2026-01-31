package com.edu.finance.payment.gateway;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;
import com.edu.finance.payment.PaymentGateway;
import com.edu.finance.payment.config.UnionPayConfig;
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
import java.util.stream.Collectors;

/**
 * 银联支付网关实现
 * 注意: 这是一个简化的实现示例，生产环境需要使用官方SDK
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "payment.unionpay", name = "enabled", havingValue = "true")
public class UnionPaymentGateway implements PaymentGateway {

    private final UnionPayConfig config;

    @Override
    public String getChannel() {
        return "unionpay";
    }

    @Override
    public OnlinePaymentResponse createPayment(OnlinePaymentRequest request) {
        try {
            log.info("创建银联支付订单: paymentNo={}, amount={}, scene={}",
                    request.getPaymentId(), request.getAmount(), request.getPaymentScene());

            // 构建请求参数
            Map<String, String> params = buildPaymentParams(request);

            // 生成签名
            String sign = generateSign(params);
            params.put("signature", sign);

            // 发送请求
            String url = "qrcode".equals(request.getPaymentScene()) ?
                    config.getBackUrl() : config.getFrontUrl();
            String response = HttpUtil.post(url, params);

            // 解析响应
            Map<String, String> result = parseResponse(response);

            return parsePaymentResponse(result, request);

        } catch (Exception e) {
            log.error("创建银联支付订单失败", e);
            return OnlinePaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .errorMsg("创建支付订单失败: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentNotification queryPayment(String outTradeNo) {
        try {
            log.info("查询银联支付订单: outTradeNo={}", outTradeNo);

            Map<String, String> params = new HashMap<>();
            params.put("version", "5.1.0");
            params.put("encoding", "UTF-8");
            params.put("signMethod", "01");
            params.put("txnType", "00");
            params.put("txnSubType", "00");
            params.put("bizType", "000000");
            params.put("merId", config.getMerId());
            params.put("orderId", outTradeNo);
            params.put("txnTime", DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss"));

            // 生成签名
            String sign = generateSign(params);
            params.put("signature", sign);

            // 发送请求
            String response = HttpUtil.post(config.getSingleQueryUrl(), params);
            Map<String, String> result = parseResponse(response);

            return parseQueryResponse(result);

        } catch (Exception e) {
            log.error("查询银联支付订单失败", e);
            return null;
        }
    }

    @Override
    public boolean closePayment(String outTradeNo) {
        // 银联不支持关闭订单，订单会自动过期
        log.info("银联支付不支持主动关闭订单: outTradeNo={}", outTradeNo);
        return true;
    }

    @Override
    public PaymentNotification parseNotification(Map<String, String> params) {
        try {
            String respCode = params.get("respCode");
            boolean success = "00".equals(respCode);

            return PaymentNotification.builder()
                    .paymentChannel("unionpay")
                    .outTradeNo(params.get("orderId"))
                    .transactionNo(params.get("queryId"))
                    .amount(new BigDecimal(params.get("txnAmt")).divide(new BigDecimal("100")))
                    .status(success ? "success" : "failed")
                    .payTime(parseUnionPayTime(params.get("txnTime")))
                    .rawData(params.toString())
                    .errorCode(params.get("respCode"))
                    .errorMsg(params.get("respMsg"))
                    .build();

        } catch (Exception e) {
            log.error("解析银联支付回调失败", e);
            return null;
        }
    }

    @Override
    public boolean verifyNotification(Map<String, String> params) {
        try {
            String signature = params.get("signature");
            if (StrUtil.isBlank(signature)) {
                return false;
            }

            // 移除签名参数
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("signature");

            // 生成待验签字符串
            String content = verifyParams.entrySet().stream()
                    .filter(e -> StrUtil.isNotBlank(e.getValue()))
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            // 实际应使用银联公钥验签
            log.info("验证银联回调签名: content={}", content);
            return true;

        } catch (Exception e) {
            log.error("验证银联支付回调签名失败", e);
            return false;
        }
    }

    @Override
    public String generateNotificationResponse(boolean success) {
        return success ? "ok" : "fail";
    }

    /**
     * 构建支付参数
     */
    private Map<String, String> buildPaymentParams(OnlinePaymentRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("version", "5.1.0");
        params.put("encoding", "UTF-8");
        params.put("signMethod", "01");
        params.put("txnType", "01");
        params.put("txnSubType", "01");
        params.put("bizType", "000000");
        params.put("channelType", getChannelType(request.getPaymentScene()));
        params.put("merId", config.getMerId());
        params.put("orderId", "SK" + request.getPaymentId());
        params.put("txnTime", DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss"));
        params.put("txnAmt", request.getAmount().multiply(new BigDecimal("100")).toString());
        params.put("currencyCode", "156");
        params.put("backUrl", StrUtil.isNotBlank(request.getNotifyUrl()) ?
                request.getNotifyUrl() : config.getNotifyUrl());

        if (StrUtil.isNotBlank(request.getReturnUrl())) {
            params.put("frontUrl", request.getReturnUrl());
        }

        return params;
    }

    /**
     * 获取渠道类型
     */
    private String getChannelType(String scene) {
        return switch (StrUtil.nullToDefault(scene, "pc")) {
            case "mobile" -> "08";
            case "qrcode" -> "07";
            default -> "07";
        };
    }

    /**
     * 生成签名
     */
    private String generateSign(Map<String, String> params) {
        // 排序并拼接参数
        String content = new TreeMap<>(params).entrySet().stream()
                .filter(e -> StrUtil.isNotBlank(e.getValue()) && !"signature".equals(e.getKey()))
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // 实际应使用证书签名
        return DigestUtil.sha256Hex(content + config.getCertId());
    }

    /**
     * 解析响应
     */
    private Map<String, String> parseResponse(String response) {
        Map<String, String> result = new HashMap<>();
        if (StrUtil.isBlank(response)) {
            return result;
        }

        String[] pairs = response.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                result.put(kv[0], kv[1]);
            }
        }
        return result;
    }

    /**
     * 解析支付响应
     */
    private OnlinePaymentResponse parsePaymentResponse(Map<String, String> result, OnlinePaymentRequest request) {
        String respCode = result.get("respCode");
        if (!"00".equals(respCode)) {
            return OnlinePaymentResponse.builder()
                    .paymentId(request.getPaymentId())
                    .errorMsg(result.get("respMsg"))
                    .build();
        }

        String scene = StrUtil.nullToDefault(request.getPaymentScene(), "pc");
        String credential = "qrcode".equals(scene) ?
                result.get("qrCode") : result.get("tn");

        return OnlinePaymentResponse.builder()
                .paymentId(request.getPaymentId())
                .paymentNo("SK" + request.getPaymentId())
                .channelOrderNo(result.get("tn"))
                .paymentChannel("unionpay")
                .paymentScene(scene)
                .paymentCredential(credential)
                .credentialType("qrcode".equals(scene) ? "qrcode" : "form")
                .expireSeconds(900)
                .build();
    }

    /**
     * 解析查询响应
     */
    private PaymentNotification parseQueryResponse(Map<String, String> result) {
        String respCode = result.get("respCode");
        if (!"00".equals(respCode)) {
            return null;
        }

        String origRespCode = result.get("origRespCode");
        boolean success = "00".equals(origRespCode);

        return PaymentNotification.builder()
                .paymentChannel("unionpay")
                .outTradeNo(result.get("orderId"))
                .transactionNo(result.get("queryId"))
                .amount(new BigDecimal(result.get("txnAmt")).divide(new BigDecimal("100")))
                .status(success ? "success" : "failed")
                .payTime(parseUnionPayTime(result.get("txnTime")))
                .errorCode(result.get("origRespCode"))
                .errorMsg(result.get("origRespMsg"))
                .build();
    }

    /**
     * 解析银联时间格式
     */
    private LocalDateTime parseUnionPayTime(String timeStr) {
        if (StrUtil.isBlank(timeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        } catch (Exception e) {
            log.warn("解析银联时间失败: {}", timeStr);
            return null;
        }
    }
}
