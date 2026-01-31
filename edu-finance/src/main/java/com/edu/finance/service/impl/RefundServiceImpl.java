package com.edu.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.exception.BusinessException;
import com.edu.finance.domain.dto.RefundApplyDTO;
import com.edu.finance.domain.dto.RefundApproveDTO;
import com.edu.finance.domain.dto.RefundCalculationDTO;
import com.edu.finance.domain.entity.ClassHourAccount;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.domain.entity.Refund;
import com.edu.finance.event.RefundCompletedEvent;
import com.edu.finance.mapper.RefundMapper;
import com.edu.finance.service.ClassHourAccountService;
import com.edu.finance.service.ContractService;
import com.edu.finance.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 退费申请服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundServiceImpl extends ServiceImpl<RefundMapper, Refund> implements RefundService {

    private final ContractService contractService;
    private final ClassHourAccountService classHourAccountService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 默认违约金比例（5%）
     */
    private static final BigDecimal DEFAULT_PENALTY_RATE = new BigDecimal("0.05");

    @Override
    public IPage<Refund> pageList(IPage<Refund> page, Refund query) {
        return baseMapper.selectRefundPage(page, query);
    }

    @Override
    public RefundCalculationDTO calculateRefundAmount(Long contractId) {
        // 查询合同信息
        Contract contract = contractService.getById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 检查合同状态
        if (!"signed".equals(contract.getStatus()) && !"completed".equals(contract.getStatus())) {
            throw new BusinessException("只有已签署或已完成的合同才能申请退费");
        }

        // 查询课时账户信息
        LambdaQueryWrapper<ClassHourAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassHourAccount::getContractId, contractId);
        List<ClassHourAccount> accounts = classHourAccountService.list(wrapper);

        // 计算总课时和已消耗课时
        BigDecimal totalHours = BigDecimal.ZERO;
        BigDecimal usedHours = BigDecimal.ZERO;
        BigDecimal remainingHours = BigDecimal.ZERO;

        for (ClassHourAccount account : accounts) {
            totalHours = totalHours.add(account.getTotalHours() != null ? account.getTotalHours() : BigDecimal.ZERO);
            usedHours = usedHours.add(account.getUsedHours() != null ? account.getUsedHours() : BigDecimal.ZERO);
            remainingHours = remainingHours.add(account.getRemainingHours() != null ? account.getRemainingHours() : BigDecimal.ZERO);
        }

        // 如果没有课时账户，使用合同的总课时
        if (totalHours.compareTo(BigDecimal.ZERO) == 0 && contract.getTotalHours() != null) {
            totalHours = new BigDecimal(contract.getTotalHours());
            remainingHours = totalHours;
        }

        // 计算单课时价格
        BigDecimal pricePerHour = BigDecimal.ZERO;
        if (totalHours.compareTo(BigDecimal.ZERO) > 0) {
            pricePerHour = contract.getPaidAmount().divide(totalHours, 2, RoundingMode.HALF_UP);
        }

        // 计算已消耗金额
        BigDecimal usedAmount = pricePerHour.multiply(usedHours).setScale(2, RoundingMode.HALF_UP);

        // 计算违约金（基于剩余金额的比例）
        BigDecimal remainingAmount = contract.getPaidAmount().subtract(usedAmount);
        BigDecimal penaltyAmount = remainingAmount.multiply(DEFAULT_PENALTY_RATE).setScale(2, RoundingMode.HALF_UP);

        // 计算可退金额 = 已支付金额 - 已消耗金额 - 违约金
        BigDecimal refundableAmount = remainingAmount.subtract(penaltyAmount);
        if (refundableAmount.compareTo(BigDecimal.ZERO) < 0) {
            refundableAmount = BigDecimal.ZERO;
        }

        // 构建计算结果
        RefundCalculationDTO result = new RefundCalculationDTO();
        result.setContractAmount(contract.getAmount());
        result.setPaidAmount(contract.getPaidAmount());
        result.setTotalHours(totalHours);
        result.setUsedHours(usedHours);
        result.setRemainingHours(remainingHours);
        result.setPricePerHour(pricePerHour);
        result.setUsedAmount(usedAmount);
        result.setPenaltyAmount(penaltyAmount);
        result.setPenaltyRate(DEFAULT_PENALTY_RATE.multiply(new BigDecimal("100")));
        result.setRefundableAmount(refundableAmount);

        // 生成计算说明
        String note = String.format(
                "合同金额：%.2f元，已支付：%.2f元，总课时：%.2f，已消耗：%.2f，剩余：%.2f，" +
                        "单价：%.2f元/课时，已消耗金额：%.2f元，违约金（%.0f%%）：%.2f元，可退金额：%.2f元",
                contract.getAmount(), contract.getPaidAmount(), totalHours, usedHours, remainingHours,
                pricePerHour, usedAmount, result.getPenaltyRate(), penaltyAmount, refundableAmount
        );
        result.setCalculationNote(note);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyRefund(RefundApplyDTO applyDTO) {
        // 查询合同信息
        Contract contract = contractService.getById(applyDTO.getContractId());
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 检查合同状态
        if (!"signed".equals(contract.getStatus()) && !"completed".equals(contract.getStatus())) {
            throw new BusinessException("只有已签署或已完成的合同才能申请退费");
        }

        // 检查是否已有待审批的退费申请
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Refund::getContractId, applyDTO.getContractId())
                .eq(Refund::getStatus, "pending");
        long count = count(wrapper);
        if (count > 0) {
            throw new BusinessException("该合同已有待审批的退费申请，请勿重复提交");
        }

        // 计算退费金额
        RefundCalculationDTO calculation = calculateRefundAmount(applyDTO.getContractId());

        // 创建退费申请
        Refund refund = new Refund();
        refund.setRefundNo(generateRefundNo());
        refund.setContractId(applyDTO.getContractId());
        refund.setStudentId(contract.getStudentId());
        refund.setCampusId(contract.getCampusId());
        refund.setApplyAmount(calculation.getRefundableAmount());
        refund.setActualAmount(BigDecimal.ZERO);
        refund.setPenaltyAmount(calculation.getPenaltyAmount());
        refund.setReason(applyDTO.getReason());
        refund.setDescription(applyDTO.getDescription());
        refund.setStatus("pending");
        refund.setApplyTime(LocalDateTime.now());

        save(refund);

        log.info("退费申请提交成功，退费单号：{}，合同ID：{}，申请金额：{}",
                refund.getRefundNo(), refund.getContractId(), refund.getApplyAmount());

        return refund.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveRefund(RefundApproveDTO approveDTO) {
        // 查询退费申请
        Refund refund = getById(approveDTO.getRefundId());
        if (refund == null) {
            throw new BusinessException("退费申请不存在");
        }

        // 检查状态
        if (!"pending".equals(refund.getStatus())) {
            throw new BusinessException("只有待审批状态的申请才能审批");
        }

        // 查询合同
        Contract contract = contractService.getById(refund.getContractId());
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 更新退费申请状态
        refund.setStatus(approveDTO.getApproveResult());
        refund.setApproveTime(LocalDateTime.now());
        refund.setApproveRemark(approveDTO.getApproveRemark());

        if ("approved".equals(approveDTO.getApproveResult())) {
            // 审批通过
            if (approveDTO.getActualAmount() == null) {
                throw new BusinessException("审批通过时必须填写实际退费金额");
            }

            // 验证实际退费金额
            if (approveDTO.getActualAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("实际退费金额必须大于0");
            }

            if (approveDTO.getActualAmount().compareTo(refund.getApplyAmount()) > 0) {
                throw new BusinessException("实际退费金额不能大于申请金额");
            }

            refund.setActualAmount(approveDTO.getActualAmount());

            // 更新合同状态为已退费
            contract.setStatus("refunded");
            contractService.updateById(contract);

            // 冻结相关课时账户
            LambdaQueryWrapper<ClassHourAccount> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClassHourAccount::getContractId, refund.getContractId());
            List<ClassHourAccount> accounts = classHourAccountService.list(wrapper);
            for (ClassHourAccount account : accounts) {
                classHourAccountService.freezeAccount(account.getId());
            }

            log.info("退费申请审批通过，退费单号：{}，实际退费金额：{}",
                    refund.getRefundNo(), refund.getActualAmount());
        } else if ("rejected".equals(approveDTO.getApproveResult())) {
            // 审批拒绝
            log.info("退费申请被拒绝，退费单号：{}，拒绝原因：{}",
                    refund.getRefundNo(), approveDTO.getApproveRemark());
        } else {
            throw new BusinessException("无效的审批结果");
        }

        updateById(refund);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executeRefund(Long refundId) {
        // 查询退费申请
        Refund refund = getById(refundId);
        if (refund == null) {
            throw new BusinessException("退费申请不存在");
        }

        // 检查状态
        if (!"approved".equals(refund.getStatus())) {
            throw new BusinessException("只有已通过审批的申请才能执行退款");
        }

        // 更新退费状态
        refund.setStatus("refunded");
        refund.setRefundTime(LocalDateTime.now());
        // 这里可以设置退款方式和交易号，实际应该对接支付系统
        refund.setRefundMethod("bank");

        updateById(refund);

        // 发布退费完成事件
        RefundCompletedEvent event = new RefundCompletedEvent(
                this,
                refund.getId(),
                refund.getContractId(),
                refund.getStudentId(),
                refund.getCampusId(),
                refund.getActualAmount()
        );
        eventPublisher.publishEvent(event);

        log.info("退款执行成功，退费单号：{}，退款金额：{}",
                refund.getRefundNo(), refund.getActualAmount());

        return true;
    }

    @Override
    public String generateRefundNo() {
        String prefix = "TF" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Refund::getRefundNo, prefix)
                .orderByDesc(Refund::getRefundNo)
                .last("LIMIT 1");
        Refund lastRefund = getOne(wrapper);

        int seq = 1;
        if (lastRefund != null && lastRefund.getRefundNo() != null) {
            String lastNo = lastRefund.getRefundNo();
            if (lastNo.length() > prefix.length()) {
                seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
            }
        }
        return prefix + String.format("%04d", seq);
    }
}
