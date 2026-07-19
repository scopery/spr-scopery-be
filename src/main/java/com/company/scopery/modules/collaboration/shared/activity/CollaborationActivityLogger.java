package com.company.scopery.modules.collaboration.shared.activity;
import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;
@Component
public class CollaborationActivityLogger {
    private final ActivityLogService s;
    public CollaborationActivityLogger(ActivityLogService s) { this.s = s; }
    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        s.logSuccess(CollaborationModuleCodes.COLLABORATION, entityType,
                entityId == null ? null : entityId.toString(), action, null, null, message, null);
    }
}
