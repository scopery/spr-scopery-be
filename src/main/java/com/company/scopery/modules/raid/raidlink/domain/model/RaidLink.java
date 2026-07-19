package com.company.scopery.modules.raid.raidlink.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RaidLink(
        UUID id,
        UUID raidItemId,
        UUID projectId,
        String linkType,
        String targetType,
        UUID targetId,
        int version,
        Instant createdAt
) {
    public static RaidLink create(UUID raidItemId, UUID projectId, String linkType, String targetType, UUID targetId) {
        return new RaidLink(UUID.randomUUID(), raidItemId, projectId, linkType, targetType, targetId, 0, Instant.now());
    }
}
