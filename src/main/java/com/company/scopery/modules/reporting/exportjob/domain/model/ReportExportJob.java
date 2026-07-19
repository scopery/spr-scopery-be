package com.company.scopery.modules.reporting.exportjob.domain.model;

import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportFormat;
import com.company.scopery.modules.reporting.exportjob.domain.enums.ReportExportStatus;

import java.time.Instant;
import java.util.UUID;

public record ReportExportJob(UUID id, UUID reportRunId, UUID reportSnapshotId, UUID reportDefinitionId,
        UUID workspaceId, UUID projectId, UUID actorUserId, ReportExportFormat format, ReportExportStatus status,
        String fileName, String fileMimeType, Long fileSizeBytes, String storageKey, String contentText,
        Instant downloadExpiresAt, String filtersJson, String selectedFieldsJson, String maskingSummaryJson,
        String errorCode, String errorMessage, Instant createdAt, Instant completedAt, String traceId, int version) {

    public static ReportExportJob create(UUID runId, UUID snapshotId, UUID definitionId, UUID workspaceId, UUID projectId,
                                         UUID actorUserId, ReportExportFormat format, String fileName, String contentText,
                                         String maskingSummaryJson, String traceId) {
        Instant now = Instant.now();
        return new ReportExportJob(UUID.randomUUID(), runId, snapshotId, definitionId, workspaceId, projectId, actorUserId,
                format, ReportExportStatus.COMPLETED, fileName,
                format == ReportExportFormat.CSV ? "text/csv" : "application/json",
                contentText == null ? 0L : (long) contentText.length(), "inline:" + UUID.randomUUID(),
                contentText, now.plusSeconds(3600), null, null, maskingSummaryJson, null, null, now, now, traceId, 0);
    }

    public boolean isDownloadable() {
        return status == ReportExportStatus.COMPLETED
                && contentText != null
                && (downloadExpiresAt == null || downloadExpiresAt.isAfter(Instant.now()));
    }

    public boolean isCancellable() {
        return status == ReportExportStatus.PENDING || status == ReportExportStatus.RUNNING;
    }

    public ReportExportJob cancel() {
        if (status == ReportExportStatus.CANCELLED) {
            return this;
        }
        if (!isCancellable()) {
            throw new IllegalStateException("Export job cannot be cancelled in status " + status);
        }
        return new ReportExportJob(
                id, reportRunId, reportSnapshotId, reportDefinitionId, workspaceId, projectId, actorUserId,
                format, ReportExportStatus.CANCELLED, fileName, fileMimeType, fileSizeBytes, storageKey, contentText,
                downloadExpiresAt, filtersJson, selectedFieldsJson, maskingSummaryJson, errorCode, errorMessage,
                createdAt, completedAt, traceId, version);
    }
}
