package com.company.scopery.modules.resourcecapacity.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CapacityActivityLogger {

    private final ActivityLogService activityLogService;

    public CapacityActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                CapacityModuleCodes.CAPACITY, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
