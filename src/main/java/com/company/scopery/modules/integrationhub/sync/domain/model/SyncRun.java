package com.company.scopery.modules.integrationhub.sync.domain.model;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record SyncRun(
        UUID id,
        UUID workspaceId,
        UUID syncJobId,
        String status,
        Instant startedAt,
        Instant completedAt,
        long processedCount,
        int version,
        Instant createdAt) {

    public static SyncRun start(UUID workspaceId, UUID syncJobId) {
        Instant now = Instant.now();
        return new SyncRun(UUID.randomUUID(), workspaceId, syncJobId, "RUNNING", now, null, 0, 0, now);
    }

    public SyncRun complete(long processed, boolean success) {
        return new SyncRun(id, workspaceId, syncJobId, success ? "COMPLETED" : "FAILED",
                startedAt, Instant.now(), processed, version, createdAt);
    }
}
