package com.edu.notification.event;

import java.util.Map;

/**
 * 合同签署事件
 */
public class ContractSignedEvent extends BusinessEvent {

    public ContractSignedEvent(Object source, Map<String, Object> eventData, Long campusId, Long contractId) {
        super(source, "CONTRACT_SIGNED", eventData, campusId, contractId);
    }
}
