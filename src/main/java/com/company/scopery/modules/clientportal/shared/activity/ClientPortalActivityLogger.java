package com.company.scopery.modules.clientportal.shared.activity;
import com.company.scopery.common.audit.ActivityLogService; import com.company.scopery.modules.clientportal.shared.constant.ClientPortalModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class ClientPortalActivityLogger {
    private final ActivityLogService s; public ClientPortalActivityLogger(ActivityLogService s){this.s=s;}
    public void logSuccess(String entityType, UUID entityId, String action, String message){
        s.logSuccess(ClientPortalModuleCodes.CLIENT_PORTAL, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}
