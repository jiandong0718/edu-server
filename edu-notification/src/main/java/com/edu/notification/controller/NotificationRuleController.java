package com.edu.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.common.core.Result;
import com.edu.notification.domain.dto.NotificationRuleDTO;
import com.edu.notification.domain.dto.NotificationRuleQueryDTO;
import com.edu.notification.domain.vo.NotificationRuleVO;
import com.edu.notification.service.NotificationRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知规则Controller
 */
@Tag(name = "通知规则管理")
@RestController
@RequestMapping("/notification/rule")
@RequiredArgsConstructor
public class NotificationRuleController {

    private final NotificationRuleService notificationRuleService;

    @Operation(summary = "分页查询规则")
    @GetMapping("/page")
    public Result<IPage<NotificationRuleVO>> page(
            @Parameter(description = "查询条件") NotificationRuleQueryDTO queryDTO,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        IPage<NotificationRuleVO> page = notificationRuleService.page(queryDTO, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取规则列表")
    @GetMapping("/list")
    public Result<List<NotificationRuleVO>> list(@Parameter(description = "查询条件") NotificationRuleQueryDTO queryDTO) {
        List<NotificationRuleVO> list = notificationRuleService.list(queryDTO);
        return Result.success(list);
    }

    @Operation(summary = "获取规则详情")
    @GetMapping("/{id}")
    public Result<NotificationRuleVO> getById(@Parameter(description = "规则ID") @PathVariable Long id) {
        NotificationRuleVO vo = notificationRuleService.getById(id);
        return Result.success(vo);
    }

    @Operation(summary = "创建规则")
    @PostMapping
    public Result<Long> create(@Validated @RequestBody NotificationRuleDTO dto) {
        Long id = notificationRuleService.create(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新规则")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @Validated @RequestBody NotificationRuleDTO dto) {
        notificationRuleService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除规则")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "规则ID") @PathVariable Long id) {
        notificationRuleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "启用/禁用规则")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam String status) {
        notificationRuleService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取事件类型列表")
    @GetMapping("/event-types")
    public Result<List<Map<String, String>>> getEventTypes() {
        List<Map<String, String>> eventTypes = List.of(
                createEventType("STUDENT_REGISTER", "学员注册"),
                createEventType("CONTRACT_SIGNED", "合同签署"),
                createEventType("PAYMENT_SUCCESS", "支付成功"),
                createEventType("CLASS_REMIND", "上课提醒"),
                createEventType("ATTENDANCE_ABSENT", "缺勤"),
                createEventType("CLASS_HOUR_LOW", "课时不足"),
                createEventType("TRIAL_LESSON", "试听预约"),
                createEventType("CONTRACT_EXPIRE", "合同到期")
        );
        return Result.success(eventTypes);
    }

    @Operation(summary = "获取通知类型列表")
    @GetMapping("/notification-types")
    public Result<List<Map<String, String>>> getNotificationTypes() {
        List<Map<String, String>> notificationTypes = List.of(
                createEventType("SMS", "短信"),
                createEventType("EMAIL", "邮件"),
                createEventType("WECHAT", "微信"),
                createEventType("SYSTEM", "站内信")
        );
        return Result.success(notificationTypes);
    }

    @Operation(summary = "获取接收人类型列表")
    @GetMapping("/receiver-types")
    public Result<List<Map<String, String>>> getReceiverTypes() {
        List<Map<String, String>> receiverTypes = List.of(
                createEventType("STUDENT", "学员"),
                createEventType("PARENT", "家长"),
                createEventType("TEACHER", "教师"),
                createEventType("ADVISOR", "顾问"),
                createEventType("ADMIN", "管理员")
        );
        return Result.success(receiverTypes);
    }

    @Operation(summary = "获取发送时间类型列表")
    @GetMapping("/send-time-types")
    public Result<List<Map<String, String>>> getSendTimeTypes() {
        List<Map<String, String>> sendTimeTypes = List.of(
                createEventType("IMMEDIATE", "立即发送"),
                createEventType("SCHEDULED", "定时发送"),
                createEventType("DELAYED", "延迟发送")
        );
        return Result.success(sendTimeTypes);
    }

    /**
     * 创建事件类型映射
     */
    private Map<String, String> createEventType(String value, String label) {
        Map<String, String> map = new HashMap<>();
        map.put("value", value);
        map.put("label", label);
        return map;
    }
}
