package com.company.scopery.modules.project.templateversion.application.response;

import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateVersionResponse(
        UUID id,
        UUID projectTemplateId,
        int versionNumber,
        String name,
        String description,
        String status,
        Instant publishedAt,
        UUID publishedBy,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectTemplateVersionResponse from(ProjectTemplateVersion v) {
        return new ProjectTemplateVersionResponse(
                v.id(), v.projectTemplateId(), v.versionNumber(),
                v.name(), v.description(),
                v.status() != null ? v.status().name() : null,
                v.publishedAt(), v.publishedBy(),
                v.archivedAt(), v.archivedBy(),
                v.version(), v.createdAt(), v.updatedAt()
        );
    }
}
