package com.company.scopery.modules.specpack.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.specpack.shared.constant.SpecPackModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SpecPackActivityLogger {

    private final ActivityLogService activityLogService;

    public SpecPackActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                SpecPackModuleCodes.SPECPACK,
                entityType,
                entityId == null ? null : entityId.toString(),
                action,
                null,
                null,
                message,
                null
        );
    }
}
