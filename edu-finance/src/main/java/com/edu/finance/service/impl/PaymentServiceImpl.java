package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.mapper.PaymentMapper;
import com.edu.finance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 收款服务实现
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Override
    public boolean createPayment(Payment payment) {
        if (StrUtil.isBlank(payment.getPaymentNo())) {
            payment.setPaymentNo(generatePaymentNo());
        }
        payment.setStatus("pending");
        return save(payment);
    }

    @Override
    public boolean confirmPayment(Long id, String transactionNo) {
        Payment payment = getById(id);
        if (payment == null) {
            throw new BusinessException("收款记录不存在");
        }
        if (!"pending".equals(payment.getStatus())) {
            throw new BusinessException("只有待支付状态的记录才能确认收款");
        }
        payment.setStatus("paid");
        payment.setPayTime(LocalDateTime.now());
        payment.setTransactionNo(transactionNo);
        return updateById(payment);
    }

    @Override
    public String generatePaymentNo() {
        String prefix = "SK" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Payment::getPaymentNo, prefix)
                .orderByDesc(Payment::getPaymentNo)
                .last("LIMIT 1");
        Payment lastPayment = getOne(wrapper);

        int seq = 1;
        if (lastPayment != null && lastPayment.getPaymentNo() != null) {
            String lastNo = lastPayment.getPaymentNo();
            if (lastNo.length() > prefix.length()) {
                seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }
}
