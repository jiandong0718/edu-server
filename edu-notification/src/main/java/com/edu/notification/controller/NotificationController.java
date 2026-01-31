package com.edu.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.notification.domain.dto.*;
import com.edu.notification.domain.entity.Notification;
import com.edu.notification.domain.vo.BatchSendResultVO;
import com.edu.notification.domain.vo.NotificationPreviewVO;
import com.edu.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通知管理控制器
 */
@Tag(name = "通知管理")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 分页查询通知列表
     */
    @Operation(summary = "分页查询通知列表")
    @GetMapping("/page")
    public Result<IPage<Notification>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Notification query) {
        IPage<Notification> page = new Page<>(pageNum, pageSize);
        return Result.success(notificationService.getNotificationPage(page, query));
    }

    /**
     * 获取通知详情
     */
    @Operation(summary = "获取通知详情")
    @GetMapping("/{id}")
    public Result<Notification> getById(@PathVariable Long id) {
        return Result.success(notificationService.getById(id));
    }

    /**
     * 发送通知
     */
    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public Result<Void> send(@RequestBody Notification notification) {
        notificationService.send(notification);
        return Result.success();
    }

    /**
     * 批量发送通知
     */
    @Operation(summary = "批量发送通知")
    @PostMapping("/send/batch")
    public Result<BatchSendResultVO> sendBatch(@Validated @RequestBody BatchNotificationDTO dto) {
        BatchSendResultVO result = notificationService.sendBatchNotification(dto);
        return Result.success(result);
    }

    /**
     * 按分组发送通知
     */
    @Operation(summary = "按分组发送通知")
    @PostMapping("/send/group")
    public Result<BatchSendResultVO> sendToGroup(@Validated @RequestBody GroupNotificationDTO dto) {
        BatchSendResultVO result = notificationService.sendToGroup(dto);
        return Result.success(result);
    }

    /**
     * 按校区发送通知
     */
    @Operation(summary = "按校区发送通知")
    @PostMapping("/send/campus")
    public Result<BatchSendResultVO> sendToCampus(@Validated @RequestBody CampusNotificationDTO dto) {
        BatchSendResultVO result = notificationService.sendToCampus(dto);
        return Result.success(result);
    }

    /**
     * 按班级发送通知
     */
    @Operation(summary = "按班级发送通知")
    @PostMapping("/send/class")
    public Result<BatchSendResultVO> sendToClass(@Validated @RequestBody ClassNotificationDTO dto) {
        BatchSendResultVO result = notificationService.sendToClass(dto);
        return Result.success(result);
    }

    /**
     * 预览接收人列表
     */
    @Operation(summary = "预览接收人列表")
    @PostMapping("/send/preview")
    public Result<NotificationPreviewVO> previewReceivers(@Validated @RequestBody NotificationPreviewDTO dto) {
        NotificationPreviewVO result = notificationService.previewReceivers(dto);
        return Result.success(result);
    }

    /**
     * 获取发送进度
     */
    @Operation(summary = "获取发送进度")
    @GetMapping("/send/progress/{taskId}")
    public Result<BatchSendResultVO> getSendProgress(@PathVariable String taskId) {
        BatchSendResultVO result = notificationService.getSendProgress(taskId);
        return Result.success(result);
    }

    /**
     * 取消发送
     */
    @Operation(summary = "取消发送")
    @PostMapping("/send/cancel/{taskId}")
    public Result<Void> cancelSend(@PathVariable String taskId) {
        notificationService.cancelSend(taskId);
        return Result.success();
    }

    /**
     * 发送上课提醒
     */
    @Operation(summary = "发送上课提醒")
    @PostMapping("/send/class-reminder/{scheduleId}")
    public Result<Void> sendClassReminder(@PathVariable Long scheduleId) {
        notificationService.sendClassReminder(scheduleId);
        return Result.success();
    }

    /**
     * 发送作业通知
     */
    @Operation(summary = "发送作业通知")
    @PostMapping("/send/homework-notice/{homeworkId}")
    public Result<Void> sendHomeworkNotice(@PathVariable Long homeworkId) {
        notificationService.sendHomeworkNotice(homeworkId);
        return Result.success();
    }

    /**
     * 发送缴费提醒
     */
    @Operation(summary = "发送缴费提醒")
    @PostMapping("/send/payment-reminder/{contractId}")
    public Result<Void> sendPaymentReminder(@PathVariable Long contractId) {
        notificationService.sendPaymentReminder(contractId);
        return Result.success();
    }
}
