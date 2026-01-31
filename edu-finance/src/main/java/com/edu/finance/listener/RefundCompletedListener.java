package com.edu.finance.listener;

import com.edu.finance.event.RefundCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 退费完成事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefundCompletedListener {

    /**
     * 处理退费完成事件
     * 可以在这里发送通知、记录日志等
     */
    @Async
    @EventListener
    public void handleRefundCompleted(RefundCompletedEvent event) {
        log.info("退费完成事件触发 - 退费ID: {}, 合同ID: {}, 学员ID: {}, 退费金额: {}",
                event.getRefundId(),
                event.getContractId(),
                event.getStudentId(),
                event.getRefundAmount());

        // TODO: 发送退费完成通知给学员
        // TODO: 发送退费完成通知给相关管理员
        // TODO: 记录操作日志
        // TODO: 更新相关统计数据
    }
}
