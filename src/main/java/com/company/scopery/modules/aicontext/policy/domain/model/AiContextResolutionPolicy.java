package com.company.scopery.modules.aicontext.policy.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AiContextResolutionPolicy(
        UUID id,
        UUID workspaceId,
        String policyCode,
        String label,
        int maxTokens,
        boolean includeRelated,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {

    public static AiContextResolutionPolicy create(
            UUID workspaceId,
            String policyCode,
            String label,
            int maxTokens,
            boolean includeRelated
    ) {
        Instant now = Instant.now();
        return new AiContextResolutionPolicy(
                UUID.randomUUID(),
                workspaceId,
                policyCode,
                label,
                maxTokens,
                includeRelated,
                true,
                now,
                now
        );
    }

    public AiContextResolutionPolicy withEnabled(boolean enabled) {
        return new AiContextResolutionPolicy(
                id,
                workspaceId,
                policyCode,
                label,
                maxTokens,
                includeRelated,
                enabled,
                createdAt,
                Instant.now()
        );
    }
}
