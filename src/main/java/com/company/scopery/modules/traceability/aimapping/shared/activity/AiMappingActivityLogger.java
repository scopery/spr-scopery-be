package com.company.scopery.modules.traceability.aimapping.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiMappingActivityLogger {

    private final ActivityLogService activityLogService;

    public AiMappingActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                AiMappingModuleCodes.AI_MAPPING,
                entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                AiMappingModuleCodes.AI_MAPPING,
                entityType, entityId.toString(),
                action, null, null, message, metadata);
    }

    public void logSuccess(String entityType, String action, String message) {
        activityLogService.logSuccess(
                AiMappingModuleCodes.AI_MAPPING,
                entityType, null,
                action, null, null, message, null);
    }
}
