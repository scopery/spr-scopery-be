package com.company.scopery.modules.aiaction.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.aiaction.shared.constant.AiActionModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiActionActivityLogger {

    private final ActivityLogService activityLogService;

    public AiActionActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                AiActionModuleCodes.AIACTION, entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                AiActionModuleCodes.AIACTION, entityType, entityId.toString(),
                action, null, null, message, metadata);
    }

    public void logFailure(String entityType, UUID entityId, String action, String message) {
        activityLogService.logFailure(
                AiActionModuleCodes.AIACTION, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
