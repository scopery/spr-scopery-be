package com.company.scopery.modules.projectfinance.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectFinanceActivityLogger {

    private final ActivityLogService activityLogService;

    public ProjectFinanceActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                ProjectFinanceModuleCodes.PROJECT_FINANCE, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
