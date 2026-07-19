package com.company.scopery.modules.aiplanning.reviewaction.domain.model;

import com.company.scopery.modules.aiplanning.reviewaction.domain.enums.ReviewActionType;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningReviewAction(
        UUID id,
        UUID suggestionId,
        UUID suggestionItemId,
        ReviewActionType action,
        UUID actorUserId,
        String reason,
        String traceId,
        Instant createdAt
) {
    public static AiPlanningReviewAction create(
            UUID suggestionId, UUID suggestionItemId, ReviewActionType action,
            UUID actorUserId, String reason, String traceId) {
        return new AiPlanningReviewAction(
                UUID.randomUUID(), suggestionId, suggestionItemId, action, actorUserId, reason, traceId, Instant.now());
    }
}
