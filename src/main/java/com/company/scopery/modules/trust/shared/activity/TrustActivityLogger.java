package com.company.scopery.modules.trust.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.trust.shared.constant.TrustModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class TrustActivityLogger {
    private final ActivityLogService activityLogService;
    public TrustActivityLogger(ActivityLogService activityLogService){this.activityLogService=activityLogService;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        activityLogService.logSuccess(TrustModuleCodes.TRUST, entityType, entityId.toString(), action, null, null, message, null);
    }
}
