package com.company.scopery.modules.integrationhub.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class IntegrationActivityLogger {
    private final ActivityLogService activityLogService;
    public IntegrationActivityLogger(ActivityLogService s){this.activityLogService=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        activityLogService.logSuccess(IntegrationModuleCodes.INTEGRATION, entityType, entityId.toString(), action, null, null, message, null);
    }
}
