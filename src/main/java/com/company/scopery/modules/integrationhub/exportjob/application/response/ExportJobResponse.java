package com.company.scopery.modules.integrationhub.exportjob.application.response;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.ExportJob;
import java.time.Instant; import java.util.UUID;
public record ExportJobResponse(UUID id, UUID workspaceId, UUID projectId, String exportFormat, String objectScope,
        String status, long rowCount, String fileReference, UUID exportAuditLogId,
        Instant createdAt, Instant updatedAt) {
    public static ExportJobResponse from(ExportJob j) {
        return new ExportJobResponse(j.id(), j.workspaceId(), j.projectId(), j.exportFormat(), j.objectScope(),
                j.status(), j.rowCount(), j.fileReference(), j.exportAuditLogId(), j.createdAt(), j.updatedAt());
    }
}
