package com.company.scopery.modules.integrationhub.exportjob.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ExportJob(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String exportFormat,
        String objectScope,
        String status,
        long rowCount,
        String fileReference,
        UUID exportAuditLogId,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static ExportJob create(UUID workspaceId, UUID projectId, String format, String scope) {
        Instant now = Instant.now();
        return new ExportJob(
                UUID.randomUUID(),
                workspaceId,
                projectId,
                format,
                scope,
                "CREATED",
                0,
                null,
                null,
                0,
                now,
                now);
    }

    public ExportJob complete(long rows, String fileRef, UUID auditLogId) {
        return new ExportJob(
                id,
                workspaceId,
                projectId,
                exportFormat,
                objectScope,
                "COMPLETED",
                rows,
                fileRef,
                auditLogId,
                version,
                createdAt,
                Instant.now());
    }

    public ExportJob cancel() {
        if ("COMPLETED".equals(status) || "CANCELLED".equals(status)) {
            throw new IllegalStateException("cannot cancel from status: " + status);
        }
        return new ExportJob(id, workspaceId, projectId, exportFormat, objectScope, "CANCELLED",
                rowCount, fileReference, exportAuditLogId, version, createdAt, Instant.now());
    }
}
