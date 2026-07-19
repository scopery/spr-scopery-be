package com.company.scopery.modules.traceability.shared.activity;
import com.company.scopery.common.audit.ActivityLogService; import com.company.scopery.modules.traceability.shared.constant.TraceabilityModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class TraceabilityActivityLogger {
    private final ActivityLogService s; public TraceabilityActivityLogger(ActivityLogService s){this.s=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(TraceabilityModuleCodes.TRACEABILITY, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}
