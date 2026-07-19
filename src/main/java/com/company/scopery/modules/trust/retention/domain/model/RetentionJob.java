package com.company.scopery.modules.trust.retention.domain.model;
import java.time.Instant; import java.util.UUID;
public record RetentionJob(UUID id, UUID workspaceId, UUID retentionPolicyId, String jobMode, String status,
        long candidateCount, long actionedCount, long skippedLegalHoldCount, Instant completedAt, int version, Instant createdAt) {
    public static RetentionJob dryRun(UUID workspaceId, UUID policyId) {
        return new RetentionJob(UUID.randomUUID(), workspaceId, policyId, "DRY_RUN", "CREATED", 0, 0, 0, null, 0, Instant.now());
    }
    public RetentionJob complete(long candidates, long actioned, long skippedHold) {
        return new RetentionJob(id, workspaceId, retentionPolicyId, jobMode, "COMPLETED", candidates, actioned, skippedHold, Instant.now(), version, createdAt);
    }
}
