package com.company.scopery.modules.documenthub.syncedblock.application.response;

import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlock;

import java.time.Instant;
import java.util.UUID;

public record SyncedBlockResponse(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String title,
        String status,
        long currentRevisionNo,
        Integer schemaVersion,
        Instant createdAt,
        Instant updatedAt
) {
    public static SyncedBlockResponse from(SyncedBlock b) {
        return new SyncedBlockResponse(b.id(), b.workspaceId(), b.projectId(), b.title(),
                b.status().name(), b.currentRevisionNo(), b.schemaVersion(), b.createdAt(), b.updatedAt());
    }
}
