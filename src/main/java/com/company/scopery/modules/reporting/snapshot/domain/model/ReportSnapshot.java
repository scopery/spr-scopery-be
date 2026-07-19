package com.company.scopery.modules.reporting.snapshot.domain.model;
import java.time.Instant; import java.util.UUID;
public record ReportSnapshot(UUID id, UUID reportRunId, UUID reportDefinitionId, UUID workspaceId, UUID projectId,
        UUID actorUserId, String snapshotType, String dataJson, String summaryJson, String maskingSummaryJson,
        Instant generatedAt, Instant createdAt, int version) {
    public static ReportSnapshot create(UUID runId, UUID definitionId, UUID workspaceId, UUID projectId, UUID actorUserId,
                                        String snapshotType, String dataJson, String summaryJson, String maskingSummaryJson) {
        Instant now = Instant.now();
        return new ReportSnapshot(UUID.randomUUID(), runId, definitionId, workspaceId, projectId, actorUserId,
                snapshotType, dataJson, summaryJson, maskingSummaryJson, now, now, 0);
    }
}
