package com.company.scopery.modules.projectbaseline.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectBaselineActivityLogger {

    private final ActivityLogService activityLogService;

    public ProjectBaselineActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                ProjectBaselineModuleCodes.PROJECT_BASELINE, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
