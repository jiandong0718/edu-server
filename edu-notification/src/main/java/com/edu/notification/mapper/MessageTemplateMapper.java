package com.edu.notification.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.notification.domain.entity.MessageTemplate;

/**
 * 消息模板 Mapper
 */
@DS("marketing")
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {
}
