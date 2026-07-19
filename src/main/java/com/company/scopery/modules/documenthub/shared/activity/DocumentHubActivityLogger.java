package com.company.scopery.modules.documenthub.shared.activity;
import com.company.scopery.common.audit.ActivityLogService; import com.company.scopery.modules.documenthub.shared.constant.DocumentHubModuleCodes;
import org.springframework.stereotype.Component; import java.util.UUID;
@Component
public class DocumentHubActivityLogger {
    private final ActivityLogService s;
    public DocumentHubActivityLogger(ActivityLogService s) { this.s=s; }
    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        s.logSuccess(DocumentHubModuleCodes.DOCUMENT_HUB, entityType, entityId==null?null:entityId.toString(), action, null, null, message, null);
    }
}
