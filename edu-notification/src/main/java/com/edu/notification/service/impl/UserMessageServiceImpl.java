package com.edu.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.notification.domain.entity.UserMessage;
import com.edu.notification.mapper.UserMessageMapper;
import com.edu.notification.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户消息服务实现
 */
@Service
@RequiredArgsConstructor
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

    @Override
    public IPage<UserMessage> getUserMessages(IPage<UserMessage> page, Long userId, String userType, Integer isRead) {
        return baseMapper.selectUserMessagePage(page, userId, userType, isRead);
    }

    @Override
    public boolean markAsRead(Long id) {
        UserMessage message = getById(id);
        if (message == null || message.getIsRead() == 1) {
            return true;
        }

        message.setIsRead(1);
        message.setReadTime(LocalDateTime.now());
        return updateById(message);
    }

    @Override
    public boolean markAllAsRead(Long userId, String userType) {
        return update(new LambdaUpdateWrapper<UserMessage>()
                .eq(UserMessage::getUserId, userId)
                .eq(UserMessage::getUserType, userType)
                .eq(UserMessage::getIsRead, 0)
                .set(UserMessage::getIsRead, 1)
                .set(UserMessage::getReadTime, LocalDateTime.now()));
    }

    @Override
    public boolean deleteMessage(Long id) {
        UserMessage message = getById(id);
        if (message == null) {
            return true;
        }

        message.setIsDeleted(1);
        return updateById(message);
    }

    @Override
    public int countUnread(Long userId, String userType) {
        return baseMapper.countUnread(userId, userType);
    }
}
