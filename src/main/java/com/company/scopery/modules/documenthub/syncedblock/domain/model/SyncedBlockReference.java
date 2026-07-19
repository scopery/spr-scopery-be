package com.company.scopery.modules.documenthub.syncedblock.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SyncedBlockReference(
        UUID id,
        UUID syncedBlockId,
        UUID documentId,
        Instant createdAt,
        Instant updatedAt
) {
    public static SyncedBlockReference create(UUID syncedBlockId, UUID documentId) {
        Instant now = Instant.now();
        return new SyncedBlockReference(UUID.randomUUID(), syncedBlockId, documentId, now, now);
    }
}
