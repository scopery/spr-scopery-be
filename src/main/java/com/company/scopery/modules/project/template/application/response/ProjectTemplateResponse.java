package com.company.scopery.modules.project.template.application.response;

import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateResponse(
        UUID id,
        String code,
        String name,
        String description,
        String scope,
        UUID organizationId,
        UUID workspaceId,
        String category,
        String visibility,
        String status,
        UUID currentVersionId,
        boolean builtIn,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectTemplateResponse from(ProjectTemplate t) {
        return new ProjectTemplateResponse(
                t.id(), t.code(), t.name(), t.description(),
                t.scope() != null ? t.scope().name() : null,
                t.organizationId(), t.workspaceId(),
                t.category() != null ? t.category().name() : null,
                t.visibility() != null ? t.visibility().name() : null,
                t.status() != null ? t.status().name() : null,
                t.currentVersionId(), t.builtIn(),
                t.archivedAt(), t.archivedBy(),
                t.version(), t.createdAt(), t.updatedAt()
        );
    }
}
