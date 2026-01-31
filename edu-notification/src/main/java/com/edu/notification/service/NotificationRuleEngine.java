package com.edu.notification.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.edu.notification.domain.dto.SendTimeConfigDTO;
import com.edu.notification.domain.dto.TriggerConditionDTO;
import com.edu.notification.domain.entity.MessageTemplate;
import com.edu.notification.domain.entity.Notification;
import com.edu.notification.domain.entity.NotificationRule;
import com.edu.notification.event.BusinessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 通知规则引擎
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRuleEngine {

    private final NotificationRuleService notificationRuleService;
    private final NotificationService notificationService;
    private final MessageTemplateService messageTemplateService;

    /**
     * 处理业务事件
     */
    public void processEvent(BusinessEvent event) {
        try {
            log.info("处理业务事件：{}, 事件类型：{}", event.getClass().getSimpleName(), event.getEventType());

            // 获取匹配的规则
            List<NotificationRule> rules = matchRules(event);
            if (rules.isEmpty()) {
                log.debug("未找到匹配的规则，事件类型：{}", event.getEventType());
                return;
            }

            // 按优先级处理规则
            for (NotificationRule rule : rules) {
                try {
                    // 评估触发条件
                    if (evaluateCondition(rule, event)) {
                        // 发送通知
                        sendNotification(rule, event);
                    }
                } catch (Exception e) {
                    log.error("处理规则失败，规则ID：{}, 规则名称：{}", rule.getId(), rule.getRuleName(), e);
                }
            }
        } catch (Exception e) {
            log.error("处理业务事件失败", e);
        }
    }

    /**
     * 匹配规则
     */
    private List<NotificationRule> matchRules(BusinessEvent event) {
        // 获取事件类型对应的启用规则
        List<NotificationRule> rules = notificationRuleService.getActiveRulesByEventType(event.getEventType());

        // 过滤校区
        if (event.getCampusId() != null) {
            rules = rules.stream()
                    .filter(rule -> rule.getCampusId() == null || rule.getCampusId().equals(event.getCampusId()))
                    .toList();
        }

        return rules;
    }

    /**
     * 评估触发条件
     */
    private boolean evaluateCondition(NotificationRule rule, BusinessEvent event) {
        String triggerCondition = rule.getTriggerCondition();
        if (StrUtil.isBlank(triggerCondition)) {
            // 没有条件，直接触发
            return true;
        }

        try {
            TriggerConditionDTO condition = JSONUtil.toBean(triggerCondition, TriggerConditionDTO.class);
            return evaluateConditionDTO(condition, event);
        } catch (Exception e) {
            log.error("解析触发条件失败，规则ID：{}", rule.getId(), e);
            return false;
        }
    }

    /**
     * 评估条件DTO
     */
    private boolean evaluateConditionDTO(TriggerConditionDTO condition, BusinessEvent event) {
        if (condition == null || condition.getConditions() == null || condition.getConditions().isEmpty()) {
            return true;
        }

        String type = condition.getType();
        List<TriggerConditionDTO.ConditionItem> conditions = condition.getConditions();

        if ("AND".equals(type)) {
            // 所有条件都满足
            return conditions.stream().allMatch(item -> evaluateConditionItem(item, event));
        } else if ("OR".equals(type)) {
            // 任一条件满足
            return conditions.stream().anyMatch(item -> evaluateConditionItem(item, event));
        } else {
            // 默认AND
            return conditions.stream().allMatch(item -> evaluateConditionItem(item, event));
        }
    }

    /**
     * 评估单个条件项
     */
    private boolean evaluateConditionItem(TriggerConditionDTO.ConditionItem item, BusinessEvent event) {
        Object fieldValue = event.getFieldValue(item.getField());
        Object compareValue = item.getValue();
        String operator = item.getOperator();

        if (fieldValue == null) {
            return false;
        }

        try {
            switch (operator) {
                case "==":
                    return fieldValue.equals(compareValue);
                case "!=":
                    return !fieldValue.equals(compareValue);
                case ">":
                    return compareNumbers(fieldValue, compareValue) > 0;
                case "<":
                    return compareNumbers(fieldValue, compareValue) < 0;
                case ">=":
                    return compareNumbers(fieldValue, compareValue) >= 0;
                case "<=":
                    return compareNumbers(fieldValue, compareValue) <= 0;
                case "in":
                    if (compareValue instanceof List) {
                        return ((List<?>) compareValue).contains(fieldValue);
                    }
                    return false;
                case "not_in":
                    if (compareValue instanceof List) {
                        return !((List<?>) compareValue).contains(fieldValue);
                    }
                    return true;
                case "contains":
                    return fieldValue.toString().contains(compareValue.toString());
                default:
                    log.warn("不支持的操作符：{}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("评估条件失败，字段：{}, 操作符：{}", item.getField(), operator, e);
            return false;
        }
    }

    /**
     * 比较数字
     */
    private int compareNumbers(Object value1, Object value2) {
        BigDecimal num1 = new BigDecimal(value1.toString());
        BigDecimal num2 = new BigDecimal(value2.toString());
        return num1.compareTo(num2);
    }

    /**
     * 发送通知
     */
    private void sendNotification(NotificationRule rule, BusinessEvent event) {
        log.info("触发通知规则：{}, 规则编码：{}", rule.getRuleName(), rule.getRuleCode());

        // 解析通知类型
        List<String> notificationTypes = Arrays.asList(rule.getNotificationType().split(","));

        // 解析接收人类型
        List<String> receiverTypes = Arrays.asList(rule.getReceiverType().split(","));

        // 获取消息模板
        MessageTemplate template = null;
        if (rule.getTemplateId() != null) {
            template = messageTemplateService.getById(rule.getTemplateId());
        }

        // 构建通知内容
        String title = buildNotificationTitle(rule, event, template);
        String content = buildNotificationContent(rule, event, template);

        // 计算发送时间
        LocalDateTime scheduledTime = calculateSendTime(rule, event);

        // 为每种通知类型和接收人类型创建通知
        for (String notificationType : notificationTypes) {
            for (String receiverType : receiverTypes) {
                Notification notification = new Notification();
                notification.setTitle(title);
                notification.setContent(content);
                notification.setType(event.getEventType());
                notification.setChannel(mapNotificationTypeToChannel(notificationType.trim()));
                notification.setReceiverType(mapReceiverType(receiverType.trim()));
                notification.setBizType(event.getEventType());
                notification.setBizId(event.getBizId());
                notification.setCampusId(event.getCampusId());
                notification.setScheduledTime(scheduledTime);
                notification.setSendStatus("pending");

                // 保存并发送通知
                notificationService.send(notification);
            }
        }
    }

    /**
     * 构建通知标题
     */
    private String buildNotificationTitle(NotificationRule rule, BusinessEvent event, MessageTemplate template) {
        if (template != null && StrUtil.isNotBlank(template.getTitle())) {
            return replaceVariables(template.getTitle(), event.getEventData());
        }
        return rule.getRuleName();
    }

    /**
     * 构建通知内容
     */
    private String buildNotificationContent(NotificationRule rule, BusinessEvent event, MessageTemplate template) {
        if (template != null && StrUtil.isNotBlank(template.getContent())) {
            return replaceVariables(template.getContent(), event.getEventData());
        }
        return rule.getDescription();
    }

    /**
     * 替换变量
     */
    private String replaceVariables(String text, Map<String, Object> variables) {
        if (StrUtil.isBlank(text) || variables == null || variables.isEmpty()) {
            return text;
        }

        String result = text;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return result;
    }

    /**
     * 计算发送时间
     */
    private LocalDateTime calculateSendTime(NotificationRule rule, BusinessEvent event) {
        String sendTimeType = rule.getSendTimeType();
        String sendTimeConfig = rule.getSendTimeConfig();

        if ("IMMEDIATE".equals(sendTimeType)) {
            return LocalDateTime.now();
        }

        if (StrUtil.isBlank(sendTimeConfig)) {
            return LocalDateTime.now();
        }

        try {
            SendTimeConfigDTO config = JSONUtil.toBean(sendTimeConfig, SendTimeConfigDTO.class);

            if ("DELAYED".equals(config.getType())) {
                LocalDateTime baseTime = LocalDateTime.now();
                int delay = config.getDelay();
                String unit = config.getUnit();
                Boolean beforeEvent = config.getBeforeEvent();

                LocalDateTime targetTime = baseTime;
                switch (unit) {
                    case "MINUTES":
                        targetTime = beforeEvent ? baseTime.minusMinutes(delay) : baseTime.plusMinutes(delay);
                        break;
                    case "HOURS":
                        targetTime = beforeEvent ? baseTime.minusHours(delay) : baseTime.plusHours(delay);
                        break;
                    case "DAYS":
                        targetTime = beforeEvent ? baseTime.minusDays(delay) : baseTime.plusDays(delay);
                        break;
                }
                return targetTime;
            }

            return LocalDateTime.now();
        } catch (Exception e) {
            log.error("解析发送时间配置失败，规则ID：{}", rule.getId(), e);
            return LocalDateTime.now();
        }
    }

    /**
     * 映射通知类型到渠道
     */
    private String mapNotificationTypeToChannel(String notificationType) {
        switch (notificationType) {
            case "SMS":
                return "sms";
            case "EMAIL":
                return "email";
            case "WECHAT":
                return "wechat";
            case "SYSTEM":
                return "site";
            default:
                return "site";
        }
    }

    /**
     * 映射接收人类型
     */
    private String mapReceiverType(String receiverType) {
        switch (receiverType) {
            case "STUDENT":
                return "student";
            case "PARENT":
                return "parent";
            case "TEACHER":
                return "teacher";
            case "ADVISOR":
                return "user";
            case "ADMIN":
                return "user";
            default:
                return "user";
        }
    }
}
