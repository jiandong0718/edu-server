package com.edu.finance.listener;

import com.edu.finance.event.ContractPaidEvent;
import com.edu.finance.service.ClassHourAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 合同支付事件监听器
 * 监听合同支付完成事件，自动创建课时账户
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractPaidEventListener {

    private final ClassHourAccountService classHourAccountService;

    /**
     * 处理合同支付完成事件
     * 自动为学员创建课时账户
     */
    @Async
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleContractPaidEvent(ContractPaidEvent event) {
        try {
            log.info("收到合同支付完成事件: contractId={}, studentId={}",
                    event.getContractId(), event.getStudentId());

            // 根据合同创建课时账户
            boolean result = classHourAccountService.createAccountByContract(event.getContractId());

            if (result) {
                log.info("合同支付后课时账户创建成功: contractId={}", event.getContractId());
            } else {
                log.warn("合同支付后课时账户创建失败: contractId={}", event.getContractId());
            }
        } catch (Exception e) {
            log.error("处理合同支付事件失败: contractId={}, error={}",
                    event.getContractId(), e.getMessage(), e);
        }
    }
}
