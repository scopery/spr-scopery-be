package com.company.scopery.modules.finance.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.finance.shared.constant.FinanceModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FinanceActivityLogger {

    private final ActivityLogService activityLogService;

    public FinanceActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void log(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                FinanceModuleCodes.FINANCE, entityType,
                entityId == null ? null : entityId.toString(),
                action, null, null, message, null);
    }

    public void log(String entityType, String entityId, String action, String message) {
        activityLogService.logSuccess(
                FinanceModuleCodes.FINANCE, entityType, entityId,
                action, null, null, message, null);
    }
}
