package com.company.scopery.modules.integrationhub.sync.domain.model;
import java.time.Instant; import java.util.UUID;
public record SyncConflict(UUID id, UUID workspaceId, UUID syncJobId, UUID syncRunId, UUID connectionId,
        String conflictType, String scoperyObjectType, UUID scoperyObjectId, String externalObjectType, String externalId,
        String severity, String status, String description, String resolutionStrategy,
        Instant resolvedAt, UUID resolvedBy, String resolutionNotes, int version, Instant createdAt, Instant updatedAt) {

    public static SyncConflict open(UUID workspaceId, UUID syncJobId, UUID connectionId, String conflictType) {
        Instant now = Instant.now();
        return new SyncConflict(UUID.randomUUID(), workspaceId, syncJobId, null, connectionId,
                conflictType, null, null, null, null, "MEDIUM", "OPEN", null, null,
                null, null, null, 0, now, now);
    }

    public SyncConflict resolve(String strategy, String notes, UUID resolvedBy) {
        if (!"OPEN".equals(status)) throw new IllegalStateException("cannot resolve from status: " + status);
        return new SyncConflict(id, workspaceId, syncJobId, syncRunId, connectionId, conflictType, scoperyObjectType,
                scoperyObjectId, externalObjectType, externalId, severity, "RESOLVED", description, strategy,
                Instant.now(), resolvedBy, notes, version, createdAt, Instant.now());
    }

    public SyncConflict dismiss() {
        if (!"OPEN".equals(status)) throw new IllegalStateException("cannot dismiss from status: " + status);
        return new SyncConflict(id, workspaceId, syncJobId, syncRunId, connectionId, conflictType, scoperyObjectType,
                scoperyObjectId, externalObjectType, externalId, severity, "DISMISSED", description, resolutionStrategy,
                Instant.now(), resolvedBy, resolutionNotes, version, createdAt, Instant.now());
    }
}
