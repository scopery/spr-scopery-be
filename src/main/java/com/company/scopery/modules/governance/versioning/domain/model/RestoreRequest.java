package com.company.scopery.modules.governance.versioning.domain.model;
import java.time.Instant; import java.util.UUID;
public record RestoreRequest(UUID id, UUID workspaceId, UUID projectId, String objectTypeCode, UUID targetId,
        UUID restoreFromVersionRecordId, String status, String reason, Instant completedAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static RestoreRequest request(UUID workspaceId, UUID projectId, String objectType, UUID targetId, UUID fromVersionId, String reason) {
        Instant now = Instant.now();
        return new RestoreRequest(UUID.randomUUID(), workspaceId, projectId, objectType, targetId, fromVersionId, "REQUESTED", reason, null, 0, now, now);
    }
    public RestoreRequest complete() {
        Instant now = Instant.now();
        return new RestoreRequest(id, workspaceId, projectId, objectTypeCode, targetId, restoreFromVersionRecordId, "COMPLETED", reason, now, version, createdAt, now);
    }
}
