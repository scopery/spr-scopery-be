package com.company.scopery.modules.governance.versioning.domain.model;
import java.time.Instant; import java.util.UUID;
public record GovernanceSnapshot(UUID id, UUID workspaceId, String objectTypeCode, UUID targetId, String snapshotMode,
        String schemaVersion, String snapshotJson, String maskedFieldsJson, boolean sensitiveFieldsPresent,
        int version, Instant createdAt, Instant updatedAt) {
    public static GovernanceSnapshot create(UUID workspaceId, String objectType, UUID targetId, String json) {
        Instant now = Instant.now();
        return new GovernanceSnapshot(UUID.randomUUID(), workspaceId, objectType, targetId, "FULL", "1", json, null, false, 0, now, now);
    }
}
