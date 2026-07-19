package com.company.scopery.modules.aiplanning.contextsnapshot.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningContextSnapshot(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID actorUserId,
        String contextType,
        String accessScopeJson,
        String includedSectionsJson,
        String redactionSummaryJson,
        String contextPayloadJson,
        Integer tokenEstimate,
        String traceId,
        Instant createdAt
) {
    public static AiPlanningContextSnapshot create(
            UUID projectId, UUID workspaceId, UUID actorUserId, String contextType,
            String accessScopeJson, String includedSectionsJson, String redactionSummaryJson,
            String contextPayloadJson, Integer tokenEstimate, String traceId) {
        return new AiPlanningContextSnapshot(
                UUID.randomUUID(), projectId, workspaceId, actorUserId, contextType,
                accessScopeJson, includedSectionsJson, redactionSummaryJson, contextPayloadJson,
                tokenEstimate, traceId, Instant.now());
    }
}
