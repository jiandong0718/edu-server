package com.edu.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.notification.domain.entity.MessageTemplate;
import com.edu.notification.mapper.MessageTemplateMapper;
import com.edu.notification.service.MessageTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息模板服务实现
 */
@Service
@RequiredArgsConstructor
public class MessageTemplateServiceImpl extends ServiceImpl<MessageTemplateMapper, MessageTemplate> implements MessageTemplateService {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{(\\w+)}");

    @Override
    public MessageTemplate getByCode(String code) {
        return getOne(new LambdaQueryWrapper<MessageTemplate>()
                .eq(MessageTemplate::getCode, code)
                .eq(MessageTemplate::getStatus, 1));
    }

    @Override
    public String renderContent(String templateCode, Map<String, Object> params) {
        MessageTemplate template = getByCode(templateCode);
        if (template == null) {
            return null;
        }

        String content = template.getContent();
        if (params == null || params.isEmpty()) {
            return content;
        }

        // 替换变量
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = params.get(key);
            matcher.appendReplacement(sb, value != null ? value.toString() : "");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
