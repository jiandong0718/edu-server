package com.edu.finance.listener;

import com.edu.finance.event.ContractApprovalEvent;
import com.edu.notification.domain.entity.UserMessage;
import com.edu.notification.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 合同审批事件监听器
 * 用于发送审批通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractApprovalEventListener {

    private final UserMessageService userMessageService;

    @Async
    @EventListener
    public void handleContractApprovalEvent(ContractApprovalEvent event) {
        log.info("收到合同审批事件: approvalId={}, eventType={}", event.getApprovalId(), event.getEventType());

        try {
            UserMessage message = new UserMessage();
            
            message.setUserType("staff");
            message.setSendTime(LocalDateTime.now());
            message.setIsRead(0);

            switch (event.getEventType()) {
                case "submitted":
                    // 提交审批，通知审批人
                    message.setUserId(event.getApproverId());
                    message.setTitle("待审批：合同审批通知");
                    message.setContent(String.format("您有一个新的合同审批待处理，合同编号：%s，提交人：%s",
                            event.getContractNo(), event.getSubmitterName()));
                    break;

                case "approved":
                    // 审批通过，通知提交人
                    message.setUserId(event.getSubmitterId());
                    message.setTitle("审批通过：合同审批结果");
                    message.setContent(String.format("您提交的合同（编号：%s）审批已通过，审批人：%s",
                            event.getContractNo(), event.getApproverName()));
                    break;

                case "rejected":
                    // 审批拒绝，通知提交人
                    message.setUserId(event.getSubmitterId());
                    message.setTitle("审批拒绝：合同审批结果");
                    message.setContent(String.format("您提交的合同（编号：%s）审批被拒绝，审批人：%s，原因：%s",
                            event.getContractNo(), event.getApproverName(), event.getRemark()));
                    break;

                case "returned":
                    // 审批退回，通知提交人
                    message.setUserId(event.getSubmitterId());
                    message.setTitle("审批退回：合同审批结果");
                    message.setContent(String.format("您提交的合同（编号：%s）审批被退回，审批人：%s，原因：%s",
                            event.getContractNo(), event.getApproverName(), event.getRemark()));
                    break;

                case "cancelled":
                    // 审批撤销，通知审批人
                    message.setUserId(event.getApproverId());
                    message.setTitle("审批撤销：合同审批通知");
                    message.setContent(String.format("合同（编号：%s）的审批已被提交人撤销",
                            event.getContractNo()));
                    break;

                default:
                    log.warn("未知的审批事件类型: {}", event.getEventType());
                    return;
            }

            userMessageService.save(message);
            log.info("合同审批通知发送成功: userId={}, eventType={}", message.getUserId(), event.getEventType());

        } catch (Exception e) {
            log.error("发送合同审批通知失败: approvalId={}, eventType={}", event.getApprovalId(), event.getEventType(), e);
        }
    }
}
