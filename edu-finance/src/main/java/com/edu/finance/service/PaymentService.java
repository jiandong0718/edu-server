package com.edu.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.finance.domain.entity.Payment;

import java.math.BigDecimal;

/**
 * 收款服务接口
 */
public interface PaymentService extends IService<Payment> {

    /**
     * 创建收款记录
     */
    boolean createPayment(Payment payment);

    /**
     * 确认收款
     */
    boolean confirmPayment(Long id, String transactionNo);

    /**
     * 生成收款单号
     */
    String generatePaymentNo();
}
