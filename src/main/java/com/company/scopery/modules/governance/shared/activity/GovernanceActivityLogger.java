package com.company.scopery.modules.governance.shared.activity;
import com.company.scopery.common.audit.ActivityLogService; import com.company.scopery.modules.governance.shared.constant.GovernanceModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class GovernanceActivityLogger {
    private final ActivityLogService s; public GovernanceActivityLogger(ActivityLogService s){this.s=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(GovernanceModuleCodes.GOVERNANCE, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}
