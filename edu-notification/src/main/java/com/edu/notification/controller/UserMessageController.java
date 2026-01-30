package com.edu.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.Result;
import com.edu.notification.domain.entity.UserMessage;
import com.edu.notification.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户消息控制器
 */
@RestController
@RequestMapping("/notification/message")
@RequiredArgsConstructor
public class UserMessageController {

    private final UserMessageService userMessageService;

    /**
     * 分页查询用户消息
     */
    @GetMapping("/page")
    public Result<IPage<UserMessage>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "user") String userType,
            @RequestParam(required = false) Integer isRead) {
        IPage<UserMessage> page = new Page<>(pageNum, pageSize);
        return Result.success(userMessageService.getUserMessages(page, userId, userType, isRead));
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        userMessageService.markAsRead(id);
        return Result.success();
    }

    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "user") String userType) {
        userMessageService.markAllAsRead(userId, userType);
        return Result.success();
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userMessageService.deleteMessage(id);
        return Result.success();
    }

    /**
     * 统计未读消息数量
     */
    @GetMapping("/unread-count")
    public Result<Integer> countUnread(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "user") String userType) {
        return Result.success(userMessageService.countUnread(userId, userType));
    }
}
