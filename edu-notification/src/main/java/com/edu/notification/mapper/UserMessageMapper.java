package com.edu.notification.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.edu.notification.domain.entity.UserMessage;
import org.apache.ibatis.annotations.Param;

/**
 * 用户消息 Mapper
 */
@DS("marketing")
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    /**
     * 分页查询用户消息列表
     */
    IPage<UserMessage> selectUserMessagePage(IPage<UserMessage> page,
                                              @Param("userId") Long userId,
                                              @Param("userType") String userType,
                                              @Param("isRead") Integer isRead);

    /**
     * 统计未读消息数量
     */
    int countUnread(@Param("userId") Long userId, @Param("userType") String userType);
}
