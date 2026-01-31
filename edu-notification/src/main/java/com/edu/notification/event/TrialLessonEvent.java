package com.edu.notification.event;

import java.util.Map;

/**
 * 试听预约事件
 */
public class TrialLessonEvent extends BusinessEvent {

    public TrialLessonEvent(Object source, Map<String, Object> eventData, Long campusId, Long trialId) {
        super(source, "TRIAL_LESSON", eventData, campusId, trialId);
    }
}
