package com.company.scopery.modules.aicontext.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.aicontext.shared.constant.AiContextModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class AiContextActivityLogger {
    private final ActivityLogService service;

    public AiContextActivityLogger(ActivityLogService service) { this.service = service; }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        service.logSuccess(AiContextModuleCodes.AI_CONTEXT, entityType,
                entityId == null ? null : entityId.toString(), action, null, null, message, null);
    }
}
