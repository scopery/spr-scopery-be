package com.company.scopery.modules.governance.versioning.domain.model;
import java.time.Instant; import java.util.UUID;
public record GovernanceVersionRecord(UUID id, UUID workspaceId, UUID projectId, String objectTypeCode, UUID targetId,
        String domainVersionType, UUID domainVersionId, UUID snapshotId, String changeType, String changeReason,
        boolean currentFlag, boolean finalizedFlag, boolean restoreEligible, int versionNumber,
        int version, Instant createdAt, Instant updatedAt) {
    public static GovernanceVersionRecord create(UUID workspaceId, UUID projectId, String objectType, UUID targetId,
                                                 UUID snapshotId, String changeType, String reason, int versionNumber) {
        Instant now = Instant.now();
        return new GovernanceVersionRecord(UUID.randomUUID(), workspaceId, projectId, objectType, targetId, null, null, snapshotId,
                changeType, reason, true, false, true, versionNumber, 0, now, now);
    }
    public GovernanceVersionRecord clearCurrent() {
        return new GovernanceVersionRecord(id, workspaceId, projectId, objectTypeCode, targetId, domainVersionType, domainVersionId, snapshotId,
                changeType, changeReason, false, finalizedFlag, restoreEligible, versionNumber, version, createdAt, Instant.now());
    }
    public GovernanceVersionRecord markFinalized() {
        return new GovernanceVersionRecord(id, workspaceId, projectId, objectTypeCode, targetId, domainVersionType, domainVersionId, snapshotId,
                changeType, changeReason, currentFlag, true, restoreEligible, versionNumber, version, createdAt, Instant.now());
    }
}
