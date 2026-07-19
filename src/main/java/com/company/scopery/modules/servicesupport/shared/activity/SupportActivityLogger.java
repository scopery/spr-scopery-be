package com.company.scopery.modules.servicesupport.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.servicesupport.shared.constant.SupportModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class SupportActivityLogger {
    private final ActivityLogService activityLogService;
    public SupportActivityLogger(ActivityLogService s){this.activityLogService=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        activityLogService.logSuccess(SupportModuleCodes.SUPPORT, entityType, entityId.toString(), action, null, null, message, null);
    }
}
