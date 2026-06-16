package com.company.scopery.modules.iam.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.iam.shared.constant.IamModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IamActivityLogger {

    private final ActivityLogService activityLogService;

    public IamActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                IamModuleCodes.IAM, entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                IamModuleCodes.IAM, entityType, entityId.toString(),
                action, null, null, message, metadata);
    }
}
