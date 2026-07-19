package com.company.scopery.modules.quote.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.quote.shared.constant.QuoteModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QuoteActivityLogger {

    private final ActivityLogService activityLogService;

    public QuoteActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                QuoteModuleCodes.QUOTE, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
