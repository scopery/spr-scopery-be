package com.company.scopery.modules.aiagent.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiAgentActivityLogger {

    private final ActivityLogService activityLogService;

    public AiAgentActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                AiAgentModuleCodes.AIAGENT, entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                AiAgentModuleCodes.AIAGENT, entityType, entityId.toString(),
                action, null, null, message, metadata);
    }
}
