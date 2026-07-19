package com.company.scopery.modules.externalparty.shared.activity;
import com.company.scopery.common.audit.ActivityLogService; import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ExternalPartyActivityLogger {
    private final ActivityLogService s; public ExternalPartyActivityLogger(ActivityLogService s){this.s=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(ExternalPartyModuleCodes.EXTERNAL_PARTY, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}
