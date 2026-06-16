package com.company.scopery.modules.notification.shared;

import com.company.scopery.common.audit.ActivityLogService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationActivityLogger {

    private final ActivityLogService activityLogService;

    public NotificationActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(NotificationModuleCodes.NOTIFICATION, entityType,
                entityId.toString(), action, null, null, message, null);
    }
}
