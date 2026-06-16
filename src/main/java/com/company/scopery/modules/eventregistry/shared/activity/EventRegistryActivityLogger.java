package com.company.scopery.modules.eventregistry.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventRegistryActivityLogger {

    private final ActivityLogService activityLogService;

    public EventRegistryActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(EventRegistryModuleCodes.EVENT_REGISTRY, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}