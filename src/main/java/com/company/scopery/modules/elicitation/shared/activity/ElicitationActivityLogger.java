package com.company.scopery.modules.elicitation.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ElicitationActivityLogger {

    private final ActivityLogService activityLogService;

    public ElicitationActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                ElicitationModuleCodes.ELICITATION, entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                ElicitationModuleCodes.ELICITATION, entityType, entityId.toString(),
                action, null, null, message, metadata);
    }
}
