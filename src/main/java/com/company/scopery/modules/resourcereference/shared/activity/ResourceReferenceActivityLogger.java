package com.company.scopery.modules.resourcereference.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceModuleCodes;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ResourceReferenceActivityLogger {
    private final ActivityLogService service;

    public ResourceReferenceActivityLogger(ActivityLogService service) { this.service = service; }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        service.logSuccess(ResourceReferenceModuleCodes.RESOURCE_REFERENCE, entityType,
                entityId == null ? null : entityId.toString(), action, null, null, message, null);
    }
}
