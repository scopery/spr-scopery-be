package com.company.scopery.modules.aiassistant.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiAssistantActivityLogger {

    private final ActivityLogService activityLogService;

    public AiAssistantActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                AiAssistantModuleCodes.AIASSISTANT, entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                AiAssistantModuleCodes.AIASSISTANT, entityType, entityId.toString(),
                action, null, null, message, metadata);
    }
}
