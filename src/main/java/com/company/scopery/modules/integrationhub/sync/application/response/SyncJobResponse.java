package com.company.scopery.modules.integrationhub.sync.application.response;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJob;
import java.time.Instant; import java.util.UUID;
public record SyncJobResponse(UUID id, UUID workspaceId, UUID connectionId, String name, String syncDirection,
        String syncMode, String objectScope, String conflictStrategy, String status,
        Instant createdAt, Instant updatedAt) {
    public static SyncJobResponse from(SyncJob j) {
        return new SyncJobResponse(j.id(), j.workspaceId(), j.connectionId(), j.name(), j.syncDirection(),
                j.syncMode(), j.objectScope(), j.conflictStrategy(), j.status(), j.createdAt(), j.updatedAt());
    }
}
