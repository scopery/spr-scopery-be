package com.company.scopery.modules.documenthub.syncedblock.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SyncedBlockRevision(
        UUID id,
        UUID syncedBlockId,
        long revisionNo,
        String ast,
        Instant createdAt,
        String createdBy
) {
    public static SyncedBlockRevision create(UUID syncedBlockId, long revisionNo, String ast, String createdBy) {
        return new SyncedBlockRevision(UUID.randomUUID(), syncedBlockId, revisionNo, ast, Instant.now(), createdBy);
    }
}
