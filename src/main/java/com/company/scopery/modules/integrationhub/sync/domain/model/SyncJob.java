package com.company.scopery.modules.integrationhub.sync.domain.model;
import java.time.Instant; import java.util.UUID;
public record SyncJob(UUID id, UUID workspaceId, UUID projectId, UUID connectionId, UUID mappingProfileId,
        String syncCode, String name, String syncDirection, String syncMode, String objectScope,
        String conflictStrategy, String scheduleJson, String status, Instant disabledAt, Instant archivedAt,
        int version, Instant createdAt, Instant updatedAt) {

    public static SyncJob create(UUID workspaceId, UUID connectionId, String name, String direction, String mode,
            String scope, String conflictStrategy) {
        Instant now = Instant.now();
        return new SyncJob(UUID.randomUUID(), workspaceId, null, connectionId, null,
                null, name, direction, mode, scope, conflictStrategy, null, "ACTIVE", null, null, 0, now, now);
    }

    public SyncJob update(String name, String direction, String mode, String scope, String conflictStrategy) {
        return new SyncJob(id, workspaceId, projectId, connectionId, mappingProfileId, syncCode, name, direction, mode,
                scope, conflictStrategy, scheduleJson, status, disabledAt, archivedAt, version, createdAt, Instant.now());
    }

    public SyncJob activate() {
        return new SyncJob(id, workspaceId, projectId, connectionId, mappingProfileId, syncCode, name, syncDirection, syncMode,
                objectScope, conflictStrategy, scheduleJson, "ACTIVE", null, archivedAt, version, createdAt, Instant.now());
    }

    public SyncJob disable() {
        return new SyncJob(id, workspaceId, projectId, connectionId, mappingProfileId, syncCode, name, syncDirection, syncMode,
                objectScope, conflictStrategy, scheduleJson, "DISABLED", Instant.now(), archivedAt, version, createdAt, Instant.now());
    }

    public SyncJob archive() {
        return new SyncJob(id, workspaceId, projectId, connectionId, mappingProfileId, syncCode, name, syncDirection, syncMode,
                objectScope, conflictStrategy, scheduleJson, "ARCHIVED", disabledAt, Instant.now(), version, createdAt, Instant.now());
    }
}
