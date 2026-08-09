package com.company.scopery.modules.profitability.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProfitabilityActivityLogger {

    private final ActivityLogService activityLogService;

    public ProfitabilityActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void log(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                ProfitabilityModuleCodes.PROFITABILITY, entityType,
                entityId == null ? null : entityId.toString(),
                action, null, null, message, null);
    }

    public void log(String entityType, String entityId, String action, String message) {
        activityLogService.logSuccess(
                ProfitabilityModuleCodes.PROFITABILITY, entityType, entityId,
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        log(entityType, entityId, action, message);
    }

    public void logSuccess(String entityType, String entityId, String action, String message) {
        log(entityType, entityId, action, message);
    }
}
