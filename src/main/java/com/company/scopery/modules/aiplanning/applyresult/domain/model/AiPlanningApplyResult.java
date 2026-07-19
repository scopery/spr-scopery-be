package com.company.scopery.modules.aiplanning.applyresult.domain.model;

import com.company.scopery.modules.aiplanning.applyresult.domain.enums.ApplyResultStatus;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningApplyResult(
        UUID id,
        UUID suggestionId,
        UUID suggestionItemId,
        UUID projectId,
        ApplyResultStatus status,
        String domainAction,
        String targetType,
        UUID targetId,
        String resultPayloadJson,
        String errorCode,
        String errorMessage,
        UUID createdBy,
        String traceId,
        Instant createdAt
) {
    public static AiPlanningApplyResult create(
            UUID suggestionId, UUID suggestionItemId, UUID projectId, ApplyResultStatus status,
            String domainAction, String targetType, UUID targetId, String resultPayloadJson,
            String errorCode, String errorMessage, UUID createdBy, String traceId) {
        return new AiPlanningApplyResult(
                UUID.randomUUID(), suggestionId, suggestionItemId, projectId, status, domainAction,
                targetType, targetId, resultPayloadJson, errorCode, errorMessage, createdBy, traceId, Instant.now());
    }
}
