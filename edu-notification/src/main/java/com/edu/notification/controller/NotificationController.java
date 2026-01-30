package com.edu.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.notification.domain.entity.Notification;
import com.edu.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知管理控制器
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 分页查询通知列表
     */
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
    @GetMapping("/{id}")
    public Result<Notification> getById(@PathVariable Long id) {
        return Result.success(notificationService.getById(id));
    }

    /**
     * 发送通知
     */
    @PostMapping("/send")
    public Result<Void> send(@RequestBody Notification notification) {
        notificationService.send(notification);
        return Result.success();
    }

    /**
     * 发送上课提醒
     */
    @PostMapping("/send/class-reminder/{scheduleId}")
    public Result<Void> sendClassReminder(@PathVariable Long scheduleId) {
        notificationService.sendClassReminder(scheduleId);
        return Result.success();
    }

    /**
     * 发送作业通知
     */
    @PostMapping("/send/homework-notice/{homeworkId}")
    public Result<Void> sendHomeworkNotice(@PathVariable Long homeworkId) {
        notificationService.sendHomeworkNotice(homeworkId);
        return Result.success();
    }

    /**
     * 发送缴费提醒
     */
    @PostMapping("/send/payment-reminder/{contractId}")
    public Result<Void> sendPaymentReminder(@PathVariable Long contractId) {
        notificationService.sendPaymentReminder(contractId);
        return Result.success();
    }
}
