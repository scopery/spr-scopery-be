package com.company.scopery.modules.knowledge.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KnowledgeActivityLogger {

    private final ActivityLogService activityLogService;

    public KnowledgeActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                KnowledgeModuleCodes.KNOWLEDGE, entityType, entityId.toString(),
                action, null, null, message, null);
    }
}
