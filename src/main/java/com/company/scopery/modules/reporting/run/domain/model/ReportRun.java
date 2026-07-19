package com.company.scopery.modules.reporting.run.domain.model;
import com.company.scopery.modules.reporting.run.domain.enums.ReportRunStatus;
import java.time.Instant; import java.util.UUID;
public record ReportRun(UUID id, UUID reportDefinitionId, UUID workspaceId, UUID projectId, UUID actorUserId,
        ReportRunStatus status, String filtersJson, String selectedFieldsJson, String accessSummaryJson,
        String maskingSummaryJson, String resultSummaryJson, String errorCode, String errorMessage,
        Instant startedAt, Instant completedAt, String traceId, int version, Instant createdAt, Instant updatedAt) {
    public static ReportRun create(UUID definitionId, UUID workspaceId, UUID projectId, UUID actorUserId,
                                   String filtersJson, String selectedFieldsJson, String traceId) {
        Instant now = Instant.now();
        return new ReportRun(UUID.randomUUID(), definitionId, workspaceId, projectId, actorUserId,
                ReportRunStatus.PENDING, filtersJson, selectedFieldsJson, null, null, null, null, null,
                now, null, traceId, 0, now, now);
    }
    public ReportRun markRunning() {
        return new ReportRun(id, reportDefinitionId, workspaceId, projectId, actorUserId, ReportRunStatus.RUNNING,
                filtersJson, selectedFieldsJson, accessSummaryJson, maskingSummaryJson, resultSummaryJson,
                errorCode, errorMessage, Instant.now(), completedAt, traceId, version, createdAt, Instant.now());
    }
    public ReportRun markCompleted(String accessSummaryJson, String maskingSummaryJson, String resultSummaryJson) {
        return new ReportRun(id, reportDefinitionId, workspaceId, projectId, actorUserId, ReportRunStatus.COMPLETED,
                filtersJson, selectedFieldsJson, accessSummaryJson, maskingSummaryJson, resultSummaryJson,
                null, null, startedAt, Instant.now(), traceId, version, createdAt, Instant.now());
    }
    public ReportRun cancel() {
        return new ReportRun(id, reportDefinitionId, workspaceId, projectId, actorUserId, ReportRunStatus.CANCELLED,
                filtersJson, selectedFieldsJson, accessSummaryJson, maskingSummaryJson, resultSummaryJson,
                errorCode, errorMessage, startedAt, Instant.now(), traceId, version, createdAt, Instant.now());
    }
}
