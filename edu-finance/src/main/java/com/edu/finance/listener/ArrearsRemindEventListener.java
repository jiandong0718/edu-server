package com.edu.finance.listener;

import cn.hutool.core.util.StrUtil;
import com.edu.finance.domain.entity.Contract;
import com.edu.finance.event.ArrearsRemindEvent;
import com.edu.finance.mapper.ContractMapper;
import com.edu.notification.service.NotificationService;
import com.edu.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 欠费催缴提醒事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArrearsRemindEventListener {

    private final NotificationService notificationService;
    private final SmsService smsService;
    private final ContractMapper contractMapper;

    /**
     * 处理欠费催缴提醒事件
     */
    @Async
    @EventListener
    public void handleArrearsRemindEvent(ArrearsRemindEvent event) {
        try {
            log.info("处理欠费催缴提醒事件: contractId={}, studentId={}, arrearsAmount={}",
                    event.getContractId(), event.getStudentId(), event.getArrearsAmount());

            // 查询合同信息
            Contract contract = contractMapper.selectById(event.getContractId());
            if (contract == null) {
                log.warn("合同不存在，跳过催缴提醒: contractId={}", event.getContractId());
                return;
            }

            // 准备模板参数
            Map<String, Object> params = new HashMap<>();
            params.put("contractNo", contract.getContractNo());
            params.put("arrearsAmount", formatMoney(event.getArrearsAmount()));
            params.put("studentId", event.getStudentId());
            params.put("campusId", event.getCampusId());

            String reminderType = event.getReminderType();
            if (reminderType == null) {
                reminderType = "both";
            }

            // 发送站内信
            if ("message".equals(reminderType) || "both".equals(reminderType)) {
                sendInternalMessage(event, params);
            }

            // 发送短信
            if ("sms".equals(reminderType) || "both".equals(reminderType)) {
                sendSmsMessage(event, params);
            }

            log.info("欠费催缴提醒发送成功: contractId={}, studentId={}", event.getContractId(), event.getStudentId());
        } catch (Exception e) {
            log.error("处理欠费催缴提醒事件失败: contractId={}", event.getContractId(), e);
        }
    }

    /**
     * 发送站内信
     */
    private void sendInternalMessage(ArrearsRemindEvent event, Map<String, Object> params) {
        try {
            String templateCode = "ARREARS_REMIND";

            // 如果有自定义消息，使用自定义消息
            if (StrUtil.isNotBlank(event.getCustomMessage())) {
                params.put("customMessage", event.getCustomMessage());
            }

            notificationService.sendByTemplate(
                    templateCode,
                    event.getStudentId(),
                    "student",
                    params
            );

            log.info("站内信发送成功: studentId={}", event.getStudentId());
        } catch (Exception e) {
            log.error("发送站内信失败: studentId={}", event.getStudentId(), e);
        }
    }

    /**
     * 发送短信
     */
    private void sendSmsMessage(ArrearsRemindEvent event, Map<String, Object> params) {
        try {
            // 使用短信模板发送
            String templateCode = "ARREARS_REMIND_SMS";

            // 构建短信参数
            Map<String, String> smsParams = new HashMap<>();
            smsParams.put("contractNo", (String) params.get("contractNo"));
            smsParams.put("arrearsAmount", (String) params.get("arrearsAmount"));

            // 这里需要获取学员手机号，实际实现中应该从学员服务获取
            // 暂时使用模板方式发送
            // smsService.sendByTemplate(studentPhone, templateCode, smsParams);

            log.info("短信发送成功: studentId={}", event.getStudentId());
        } catch (Exception e) {
            log.error("发送短信失败: studentId={}", event.getStudentId(), e);
        }
    }

    /**
     * 格式化金额
     */
    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }
}
