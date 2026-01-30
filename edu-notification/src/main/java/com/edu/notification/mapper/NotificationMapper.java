package com.edu.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.notification.domain.entity.Notification;
import org.apache.ibatis.annotations.Param;

/**
 * 通知消息 Mapper
 */
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 分页查询通知列表
     */
    IPage<Notification> selectNotificationPage(IPage<Notification> page, @Param("query") Notification query);
}
