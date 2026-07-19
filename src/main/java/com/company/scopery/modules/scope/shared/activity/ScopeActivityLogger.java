package com.company.scopery.modules.scope.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.scope.shared.constant.ScopeModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class ScopeActivityLogger {
    private final ActivityLogService s;
    public ScopeActivityLogger(ActivityLogService s){ this.s=s; }
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(ScopeModuleCodes.SCOPE, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}
