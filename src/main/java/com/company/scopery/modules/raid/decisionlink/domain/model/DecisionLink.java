package com.company.scopery.modules.raid.decisionlink.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DecisionLink(
        UUID id,
        UUID decisionId,
        UUID projectId,
        String linkType,
        String targetType,
        UUID targetId,
        int version,
        Instant createdAt
) {
    public static DecisionLink create(UUID decisionId, UUID projectId, String linkType, String targetType, UUID targetId) {
        return new DecisionLink(UUID.randomUUID(), decisionId, projectId, linkType, targetType, targetId, 0, Instant.now());
    }
}
