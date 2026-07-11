package com.company.scopery.modules.project.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.project.shared.constant.ProjectModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectActivityLogger {

    private final ActivityLogService activityLogService;

    public ProjectActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                ProjectModuleCodes.PROJECT, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
