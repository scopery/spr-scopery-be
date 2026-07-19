package com.company.scopery.modules.documenthub.template.application.response;
import com.company.scopery.modules.documenthub.template.domain.model.DocumentTemplate;
import java.time.Instant; import java.util.UUID;
public record DocumentTemplateResponse(UUID id, UUID workspaceId, String code, String name, String category, String status, Instant createdAt) {
    public static DocumentTemplateResponse from(DocumentTemplate e) {
        return new DocumentTemplateResponse(e.id(), e.workspaceId(), e.code(), e.name(), e.category(), e.status().name(), e.createdAt());
    }
}
