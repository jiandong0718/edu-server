package com.edu.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.notification.domain.entity.UserMessage;

/**
 * 用户消息服务接口
 */
public interface UserMessageService extends IService<UserMessage> {

    /**
     * 分页查询用户消息
     */
    IPage<UserMessage> getUserMessages(IPage<UserMessage> page, Long userId, String userType, Integer isRead);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long id);

    /**
     * 标记所有消息为已读
     */
    boolean markAllAsRead(Long userId, String userType);

    /**
     * 删除消息
     */
    boolean deleteMessage(Long id);

    /**
     * 统计未读消息数量
     */
    int countUnread(Long userId, String userType);
}
