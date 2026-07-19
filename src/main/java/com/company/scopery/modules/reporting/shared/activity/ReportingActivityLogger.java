package com.company.scopery.modules.reporting.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.reporting.shared.constant.ReportingModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class ReportingActivityLogger {
    private final ActivityLogService activityLogService;
    public ReportingActivityLogger(ActivityLogService activityLogService) { this.activityLogService = activityLogService; }
    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(ReportingModuleCodes.REPORTING, entityType,
                entityId == null ? null : entityId.toString(), action, null, null, message, null);
    }
}
