package com.company.scopery.modules.documenthub.template.domain.model;
import com.company.scopery.modules.documenthub.template.domain.enums.DocumentTemplateStatus;
import com.company.scopery.modules.documenthub.template.domain.enums.TemplateMode;
import java.time.Instant; import java.util.UUID;
public record DocumentTemplate(UUID id, UUID workspaceId, String code, String name, String description, String category,
                               DocumentTemplateStatus status, TemplateMode templateMode, UUID currentVersionId, Instant archivedAt, int version, Instant createdAt, Instant updatedAt) {
    public static DocumentTemplate create(UUID workspaceId, String code, String name, String description, String category) {
        return new DocumentTemplate(UUID.randomUUID(), workspaceId, code, name, description, category, DocumentTemplateStatus.DRAFT, TemplateMode.FILE, null, null, 0, null, null);
    }
    public static DocumentTemplate createNative(UUID workspaceId, String code, String name, String description, String category) {
        return new DocumentTemplate(UUID.randomUUID(), workspaceId, code, name, description, category, DocumentTemplateStatus.DRAFT, TemplateMode.NATIVE, null, null, 0, null, null);
    }
}
