package com.company.scopery.modules.airecommendation.shared.activity;

import com.company.scopery.common.audit.ActivityLogService;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationModuleCodes;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiRecommendationActivityLogger {

    private final ActivityLogService activityLogService;

    public AiRecommendationActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message) {
        activityLogService.logSuccess(
                AiRecommendationModuleCodes.AI_RECOMMENDATION,
                entityType, entityId.toString(),
                action, null, null, message, null);
    }

    public void logSuccess(String entityType, UUID entityId, String action, String message, String metadata) {
        activityLogService.logSuccess(
                AiRecommendationModuleCodes.AI_RECOMMENDATION,
                entityType, entityId.toString(),
                action, null, null, message, metadata);
    }
}
