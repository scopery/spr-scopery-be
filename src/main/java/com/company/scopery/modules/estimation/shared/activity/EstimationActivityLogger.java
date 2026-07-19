package com.company.scopery.modules.estimation.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.estimation.shared.constant.EstimationModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EstimationActivityLogger {

    private final ActivityLogService activityLogService;

    public EstimationActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                EstimationModuleCodes.ESTIMATION, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
