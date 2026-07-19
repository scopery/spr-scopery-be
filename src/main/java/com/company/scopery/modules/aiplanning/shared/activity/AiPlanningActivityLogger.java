package com.company.scopery.modules.aiplanning.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiPlanningActivityLogger {
    private final ActivityLogService activityLogService;

    public AiPlanningActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                AiPlanningModuleCodes.AI_PLANNING, entityType,
                entityId == null ? null : entityId.toString(),
                action, null, null, message, null);
    }
}
