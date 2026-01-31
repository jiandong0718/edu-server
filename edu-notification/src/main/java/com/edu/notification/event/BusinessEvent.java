package com.edu.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 业务事件基类
 */
@Getter
public abstract class BusinessEvent extends ApplicationEvent {

    /**
     * 事件类型
     */
    private final String eventType;

    /**
     * 事件数据
     */
    private final Map<String, Object> eventData;

    /**
     * 校区ID
     */
    private final Long campusId;

    /**
     * 业务ID
     */
    private final Long bizId;

    public BusinessEvent(Object source, String eventType, Map<String, Object> eventData, Long campusId, Long bizId) {
        super(source);
        this.eventType = eventType;
        this.eventData = eventData;
        this.campusId = campusId;
        this.bizId = bizId;
    }

    /**
     * 获取事件数据中的字段值
     */
    public Object getFieldValue(String fieldName) {
        return eventData != null ? eventData.get(fieldName) : null;
    }
}
