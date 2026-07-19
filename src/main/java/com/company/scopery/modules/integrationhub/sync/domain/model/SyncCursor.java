package com.company.scopery.modules.integrationhub.sync.domain.model;
import java.time.Instant; import java.util.UUID;
public record SyncCursor(UUID id, UUID workspaceId, UUID syncJobId, String cursorKey, String cursorValue,
        Instant lastSuccessfulSyncAt, int version) {
    public static SyncCursor create(UUID workspaceId, UUID syncJobId, String key) {
        return new SyncCursor(UUID.randomUUID(), workspaceId, syncJobId, key, null, null, 0);
    }
    public SyncCursor advance(String value) {
        return new SyncCursor(id, workspaceId, syncJobId, cursorKey, value, Instant.now(), version);
    }
}
