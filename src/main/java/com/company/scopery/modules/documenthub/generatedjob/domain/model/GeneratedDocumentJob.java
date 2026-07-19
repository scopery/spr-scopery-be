package com.company.scopery.modules.documenthub.generatedjob.domain.model;
import com.company.scopery.modules.documenthub.generatedjob.domain.enums.GeneratedDocumentJobStatus;
import java.time.Instant; import java.util.UUID;
public record GeneratedDocumentJob(UUID id, UUID workspaceId, UUID projectId, UUID templateId, UUID templateVersionId, String jobType,
                                   GeneratedDocumentJobStatus status, String sourceType, UUID sourceId, UUID outputDocumentId, String errorMessage,
                                   UUID requestedBy, Instant startedAt, Instant completedAt, int version, Instant createdAt, Instant updatedAt) {
    public static GeneratedDocumentJob create(UUID workspaceId, UUID projectId, UUID templateId, UUID templateVersionId, String jobType,
                                              String sourceType, UUID sourceId, UUID requestedBy) {
        Instant now = Instant.now();
        return new GeneratedDocumentJob(UUID.randomUUID(), workspaceId, projectId, templateId, templateVersionId, jobType,
                GeneratedDocumentJobStatus.QUEUED, sourceType, sourceId, null, null, requestedBy, null, null, 0, now, now);
    }
    public GeneratedDocumentJob markRunning() {
        return new GeneratedDocumentJob(id, workspaceId, projectId, templateId, templateVersionId, jobType, GeneratedDocumentJobStatus.RUNNING,
                sourceType, sourceId, outputDocumentId, errorMessage, requestedBy, Instant.now(), null, version, createdAt, Instant.now());
    }
    public GeneratedDocumentJob succeed(UUID outputDocumentId) {
        return new GeneratedDocumentJob(id, workspaceId, projectId, templateId, templateVersionId, jobType, GeneratedDocumentJobStatus.SUCCEEDED,
                sourceType, sourceId, outputDocumentId, null, requestedBy, startedAt, Instant.now(), version, createdAt, Instant.now());
    }
    public GeneratedDocumentJob fail(String error) {
        return new GeneratedDocumentJob(id, workspaceId, projectId, templateId, templateVersionId, jobType, GeneratedDocumentJobStatus.FAILED,
                sourceType, sourceId, outputDocumentId, error, requestedBy, startedAt, Instant.now(), version, createdAt, Instant.now());
    }
}
