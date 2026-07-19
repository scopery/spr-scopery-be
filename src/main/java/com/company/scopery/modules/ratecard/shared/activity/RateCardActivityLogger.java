package com.company.scopery.modules.ratecard.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RateCardActivityLogger {

    private final ActivityLogService activityLogService;

    public RateCardActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                RateCardModuleCodes.RATE_CARD, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
