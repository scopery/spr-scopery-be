package com.company.scopery.modules.documenthub.document.application.response;
import com.company.scopery.modules.documenthub.document.domain.model.Document; import java.time.Instant; import java.util.UUID;
public record DocumentResponse(UUID id, UUID projectId, String code, String title, String status, UUID currentVersionId, Instant createdAt) {
    public static DocumentResponse from(Document d) { return new DocumentResponse(d.id(), d.projectId(), d.code(), d.title(), d.status().name(), d.currentVersionId(), d.createdAt()); }
}
