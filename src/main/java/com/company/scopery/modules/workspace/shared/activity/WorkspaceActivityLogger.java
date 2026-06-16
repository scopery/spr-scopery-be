package com.company.scopery.modules.workspace.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkspaceActivityLogger {

    private final ActivityLogService activityLogService;

    public WorkspaceActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                WorkspaceModuleCodes.WORKSPACE, entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                WorkspaceModuleCodes.WORKSPACE, entityType, entityId.toString(),
                action, null, null, message, metadata);
    }
}
