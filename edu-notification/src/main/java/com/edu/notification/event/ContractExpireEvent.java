package com.edu.notification.event;

import java.util.Map;

/**
 * 合同到期事件
 */
public class ContractExpireEvent extends BusinessEvent {

    public ContractExpireEvent(Object source, Map<String, Object> eventData, Long campusId, Long contractId) {
        super(source, "CONTRACT_EXPIRE", eventData, campusId, contractId);
    }
}
