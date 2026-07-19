package com.company.scopery.modules.integrationhub.sync.application.response;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncConflict;
import java.time.Instant; import java.util.UUID;
public record SyncConflictResponse(UUID id, UUID syncJobId, UUID connectionId, String conflictType,
        String severity, String status, String resolutionStrategy, Instant resolvedAt, UUID resolvedBy,
        Instant createdAt, Instant updatedAt) {
    public static SyncConflictResponse from(SyncConflict c) {
        return new SyncConflictResponse(c.id(), c.syncJobId(), c.connectionId(), c.conflictType(),
                c.severity(), c.status(), c.resolutionStrategy(), c.resolvedAt(), c.resolvedBy(),
                c.createdAt(), c.updatedAt());
    }
}
