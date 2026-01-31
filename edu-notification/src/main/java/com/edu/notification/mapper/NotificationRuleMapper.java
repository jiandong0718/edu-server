package com.edu.notification.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.notification.domain.entity.NotificationRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知规则Mapper
 */
@Mapper
public interface NotificationRuleMapper extends BaseMapper<NotificationRule> {
}
