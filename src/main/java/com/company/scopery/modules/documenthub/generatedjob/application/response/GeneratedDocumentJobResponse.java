package com.company.scopery.modules.documenthub.generatedjob.application.response;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.GeneratedDocumentJob;
import java.time.Instant; import java.util.UUID;
public record GeneratedDocumentJobResponse(UUID id, UUID projectId, UUID templateId, String jobType, String status, UUID outputDocumentId, Instant createdAt, Instant completedAt) {
    public static GeneratedDocumentJobResponse from(GeneratedDocumentJob e) {
        return new GeneratedDocumentJobResponse(e.id(), e.projectId(), e.templateId(), e.jobType(), e.status().name(), e.outputDocumentId(), e.createdAt(), e.completedAt());
    }
}
