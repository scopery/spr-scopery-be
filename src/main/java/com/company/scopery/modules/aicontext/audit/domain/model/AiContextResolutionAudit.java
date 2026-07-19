package com.company.scopery.modules.aicontext.audit.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AiContextResolutionAudit(
        UUID id,
        UUID policyId,
        UUID documentId,
        UUID actorId,
        Integer tokenCount,
        Integer blockCount,
        String status,
        String errorMessage,
        Instant resolvedAt
) {

    public static AiContextResolutionAudit create(
            UUID policyId,
            UUID documentId,
            UUID actorId,
            Integer tokenCount,
            Integer blockCount
    ) {
        return new AiContextResolutionAudit(
                UUID.randomUUID(),
                policyId,
                documentId,
                actorId,
                tokenCount,
                blockCount,
                "SUCCESS",
                null,
                Instant.now()
        );
    }

    public static AiContextResolutionAudit failed(
            UUID policyId,
            UUID documentId,
            UUID actorId,
            String errorMessage
    ) {
        return new AiContextResolutionAudit(
                UUID.randomUUID(),
                policyId,
                documentId,
                actorId,
                0,
                0,
                "FAILED",
                errorMessage,
                Instant.now()
        );
    }
}
