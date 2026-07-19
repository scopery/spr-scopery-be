package com.company.scopery.modules.trust.anonymization.domain.model;
import java.time.Instant; import java.util.UUID;
public record AnonymizationPlan(UUID id, UUID workspaceId, UUID dataSubjectIndexId,
        String status, String planJson, String dryRunResultJson, boolean legalHoldBlocked,
        String reason, Instant executedAt, Instant cancelledAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static AnonymizationPlan create(UUID workspaceId, UUID dataSubjectIndexId, String planJson, String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("reason required");
        Instant now = Instant.now();
        return new AnonymizationPlan(UUID.randomUUID(), workspaceId, dataSubjectIndexId,
                "DRAFT", planJson, null, false, reason, null, null, 0, now, now);
    }
    public AnonymizationPlan completeDryRun(String dryRunResult) {
        return new AnonymizationPlan(id, workspaceId, dataSubjectIndexId, "DRY_RUN_COMPLETED",
                planJson, dryRunResult, legalHoldBlocked, reason, null, null, version, createdAt, Instant.now());
    }
    public AnonymizationPlan execute() {
        if (!"DRY_RUN_COMPLETED".equals(status)) throw new IllegalStateException("Dry-run required first");
        return new AnonymizationPlan(id, workspaceId, dataSubjectIndexId, "EXECUTED",
                planJson, dryRunResultJson, legalHoldBlocked, reason, Instant.now(), null, version, createdAt, Instant.now());
    }
    public AnonymizationPlan cancel() {
        return new AnonymizationPlan(id, workspaceId, dataSubjectIndexId, "CANCELLED",
                planJson, dryRunResultJson, legalHoldBlocked, reason, executedAt, Instant.now(), version, createdAt, Instant.now());
    }
    public AnonymizationPlan blockByLegalHold() {
        return new AnonymizationPlan(id, workspaceId, dataSubjectIndexId, "BLOCKED",
                planJson, dryRunResultJson, true, reason, null, null, version, createdAt, Instant.now());
    }
}
