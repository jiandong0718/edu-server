package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.event.ContractPaidEvent;
import com.edu.finance.mapper.PaymentMapper;
import com.edu.finance.service.ContractService;
import com.edu.finance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 收款服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final ContractService contractService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean createPayment(Payment payment) {
        if (StrUtil.isBlank(payment.getPaymentNo())) {
            payment.setPaymentNo(generatePaymentNo());
        }
        payment.setStatus("pending");
        return save(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        boolean result = updateById(payment);

        // 如果收款成功且关联了合同，检查合同是否已全额支付
        if (result && payment.getContractId() != null) {
            Contract contract = contractService.getById(payment.getContractId());
            if (contract != null) {
                // 更新合同已收金额
                BigDecimal newReceivedAmount = contract.getReceivedAmount().add(payment.getAmount());
                contract.setReceivedAmount(newReceivedAmount);
                contractService.updateById(contract);

                // 如果已收金额 >= 实付金额，发布合同支付完成事件
                if (newReceivedAmount.compareTo(contract.getPaidAmount()) >= 0) {
                    publishContractPaidEvent(contract, payment.getAmount());
                    log.info("合同已全额支付，发布支付完成事件: contractId={}", contract.getId());
                }
            }
        }

        return result;
    }

    /**
     * 发布合同支付完成事件
     */
    private void publishContractPaidEvent(Contract contract, BigDecimal paidAmount) {
        try {
            ContractPaidEvent event = new ContractPaidEvent(
                    this,
                    contract.getId(),
                    contract.getStudentId(),
                    contract.getCampusId(),
                    paidAmount
            );
            eventPublisher.publishEvent(event);
            log.info("发布合同支付完成事件: contractId={}, studentId={}",
                    contract.getId(), contract.getStudentId());
        } catch (Exception e) {
            log.error("发布合同支付完成事件失败: contractId={}, error={}",
                    contract.getId(), e.getMessage(), e);
        }
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
