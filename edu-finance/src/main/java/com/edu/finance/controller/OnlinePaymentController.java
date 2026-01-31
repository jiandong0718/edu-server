package com.edu.finance.controller;

import com.edu.common.core.R;
import com.edu.finance.domain.dto.OnlinePaymentRequest;
import com.edu.finance.domain.dto.OnlinePaymentResponse;
import com.edu.finance.domain.dto.PaymentNotification;
import com.edu.finance.service.OnlinePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 在线支付控制器
 */
@Slf4j
@Tag(name = "在线支付管理")
@RestController
@RequestMapping("/finance/online-payment")
@RequiredArgsConstructor
public class OnlinePaymentController {

    private final OnlinePaymentService onlinePaymentService;

    @Operation(summary = "创建在线支付订单")
    @PostMapping("/create")
    public R<OnlinePaymentResponse> createPayment(@RequestBody OnlinePaymentRequest request) {
        OnlinePaymentResponse response = onlinePaymentService.createPayment(request);
        return R.ok(response);
    }

    @Operation(summary = "查询支付订单状态")
    @GetMapping("/query/{paymentId}")
    public R<PaymentNotification> queryPayment(
            @Parameter(description = "收款记录ID") @PathVariable Long paymentId) {
        PaymentNotification notification = onlinePaymentService.queryPayment(paymentId);
        return R.ok(notification);
    }

    @Operation(summary = "取消支付订单")
    @PostMapping("/cancel/{paymentId}")
    public R<Boolean> cancelPayment(
            @Parameter(description = "收款记录ID") @PathVariable Long paymentId) {
        boolean result = onlinePaymentService.cancelPayment(paymentId);
        return R.ok(result);
    }

    @Operation(summary = "同步支付状态")
    @PostMapping("/sync/{paymentId}")
    public R<Boolean> syncPaymentStatus(
            @Parameter(description = "收款记录ID") @PathVariable Long paymentId) {
        boolean result = onlinePaymentService.syncPaymentStatus(paymentId);
        return R.ok(result);
    }

    @Operation(summary = "微信支付回调通知")
    @PostMapping("/notify/wechat")
    public String wechatNotify(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        return onlinePaymentService.handleNotification("wechat", params);
    }

    @Operation(summary = "支付宝支付回调通知")
    @PostMapping("/notify/alipay")
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        return onlinePaymentService.handleNotification("alipay", params);
    }

    @Operation(summary = "银联支付回调通知")
    @PostMapping("/notify/unionpay")
    public String unionpayNotify(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        return onlinePaymentService.handleNotification("unionpay", params);
    }

    /**
     * 提取请求参数
     */
    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            params.put(name, value);
        }
        return params;
    }
}
