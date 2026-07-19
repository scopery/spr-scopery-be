package com.company.scopery.modules.integrationhub.importjob.application.response;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJob;
import java.time.Instant; import java.util.UUID;
public record ImportJobResponse(UUID id, UUID workspaceId, String jobMode, String sourceFormat, String targetObjectType,
        String status, long totalRows, long validRows, long invalidRows, long createdRows,
        Instant createdAt, Instant updatedAt) {
    public static ImportJobResponse from(ImportJob j) {
        return new ImportJobResponse(j.id(), j.workspaceId(), j.jobMode(), j.sourceFormat(), j.targetObjectType(),
                j.status(), j.totalRows(), j.validRows(), j.invalidRows(), j.createdRows(),
                j.createdAt(), j.updatedAt());
    }
}
