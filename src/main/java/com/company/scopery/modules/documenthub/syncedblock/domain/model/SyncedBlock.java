package com.company.scopery.modules.documenthub.syncedblock.domain.model;

import com.company.scopery.modules.documenthub.syncedblock.domain.enums.SyncedBlockStatus;

import java.time.Instant;
import java.util.UUID;

public record SyncedBlock(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String title,
        SyncedBlockStatus status,
        long currentRevisionNo,
        Integer schemaVersion,
        Instant createdAt,
        Instant updatedAt
) {
    public static SyncedBlock create(UUID workspaceId, UUID projectId, String title, Integer schemaVersion) {
        Instant now = Instant.now();
        return new SyncedBlock(UUID.randomUUID(), workspaceId, projectId, title,
                SyncedBlockStatus.ACTIVE, 0L, schemaVersion, now, now);
    }

    public SyncedBlock withRevision(long newRevisionNo) {
        return new SyncedBlock(id, workspaceId, projectId, title, status, newRevisionNo,
                schemaVersion, createdAt, Instant.now());
    }

    public SyncedBlock archive() {
        return new SyncedBlock(id, workspaceId, projectId, title, SyncedBlockStatus.ARCHIVED,
                currentRevisionNo, schemaVersion, createdAt, Instant.now());
    }
}
