package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.ArrearsQueryDTO;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.Payment;
import com.edu.finance.domain.vo.ArrearsRemindVO;
import com.edu.finance.domain.vo.ArrearsStatisticsVO;
import com.edu.finance.domain.vo.ArrearsVO;
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
import java.util.List;

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

    @Override
    public Page<ArrearsVO> getArrearsPage(ArrearsQueryDTO query) {
        Page<ArrearsVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<ArrearsVO> result = baseMapper.selectArrearsPage(page, query);

        // 设置合同状态描述
        if (result.getRecords() != null) {
            result.getRecords().forEach(vo -> {
                vo.setContractStatusDesc(getContractStatusDesc(vo.getContractStatus()));
            });
        }

        return result;
    }

    @Override
    public ArrearsStatisticsVO getArrearsStatistics(ArrearsQueryDTO query) {
        return baseMapper.selectArrearsStatistics(query);
    }

    @Override
    public List<ArrearsRemindVO> getArrearsRemind(Integer minDays) {
        List<ArrearsRemindVO> list = baseMapper.selectArrearsRemind(minDays);

        // 设置提醒级别描述
        if (list != null) {
            list.forEach(vo -> {
                vo.setRemindLevelDesc(getRemindLevelDesc(vo.getRemindLevel()));
            });
        }

        return list;
    }

    /**
     * 获取合同状态描述
     */
    private String getContractStatusDesc(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "pending":
                return "待签署";
            case "signed":
                return "已签署";
            case "completed":
                return "已完成";
            case "refunded":
                return "已退费";
            case "cancelled":
                return "已作废";
            default:
                return status;
        }
    }

    /**
     * 获取提醒级别描述
     */
    private String getRemindLevelDesc(String level) {
        if (level == null) {
            return "";
        }
        switch (level) {
            case "normal":
                return "正常";
            case "warning":
                return "警告";
            case "urgent":
                return "紧急";
            default:
                return level;
        }
    }
}
