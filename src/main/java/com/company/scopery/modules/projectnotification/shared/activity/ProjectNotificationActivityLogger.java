package com.company.scopery.modules.projectnotification.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectNotificationActivityLogger {
    private final ActivityLogService activityLogService;

    public ProjectNotificationActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                ProjectNotificationModuleCodes.PROJECT_NOTIFICATION,
                entityType,
                entityId == null ? null : entityId.toString(),
                action, null, null, message, null);
    }
}
