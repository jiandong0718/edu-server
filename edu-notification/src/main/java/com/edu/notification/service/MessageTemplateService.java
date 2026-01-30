package com.edu.notification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.notification.domain.entity.MessageTemplate;

/**
 * 消息模板服务接口
 */
public interface MessageTemplateService extends IService<MessageTemplate> {

    /**
     * 根据编码获取模板
     */
    MessageTemplate getByCode(String code);

    /**
     * 渲染模板内容
     */
    String renderContent(String templateCode, java.util.Map<String, Object> params);
}
