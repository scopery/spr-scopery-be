package com.company.scopery.modules.reporting.exportjob.application.response;

import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;

import java.time.Instant;
import java.util.UUID;

public record ReportExportJobResponse(
        UUID id,
        UUID reportRunId,
        UUID projectId,
        UUID workspaceId,
        String format,
        String status,
        String fileName,
        String fileMimeType,
        Long fileSizeBytes,
        boolean downloadAvailable,
        Instant downloadExpiresAt,
        Instant createdAt,
        Instant completedAt
) {
    public static ReportExportJobResponse from(ReportExportJob job) {
        return new ReportExportJobResponse(
                job.id(),
                job.reportRunId(),
                job.projectId(),
                job.workspaceId(),
                job.format().name(),
                job.status().name(),
                job.fileName(),
                job.fileMimeType(),
                job.fileSizeBytes(),
                job.isDownloadable(),
                job.downloadExpiresAt(),
                job.createdAt(),
                job.completedAt());
    }
}
