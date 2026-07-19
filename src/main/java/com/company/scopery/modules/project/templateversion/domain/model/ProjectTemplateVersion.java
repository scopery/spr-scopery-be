package com.company.scopery.modules.project.templateversion.domain.model;

import com.company.scopery.modules.project.templateversion.domain.enums.ProjectTemplateVersionStatus;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateVersion(
        UUID id,
        UUID projectTemplateId,
        int versionNumber,
        String name,
        String description,
        ProjectTemplateVersionStatus status,
        Instant publishedAt,
        UUID publishedBy,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectTemplateVersion create(
            UUID projectTemplateId,
            int versionNumber,
            String name,
            String description) {
        return new ProjectTemplateVersion(
                UUID.randomUUID(),
                projectTemplateId,
                versionNumber,
                name,
                description,
                ProjectTemplateVersionStatus.DRAFT,
                null,
                null,
                null,
                null,
                0,
                null,
                null
        );
    }

    public ProjectTemplateVersion update(String name, String description) {
        return new ProjectTemplateVersion(
                this.id, this.projectTemplateId, this.versionNumber,
                name, description, this.status,
                this.publishedAt, this.publishedBy,
                this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public ProjectTemplateVersion publish(UUID actorId) {
        return new ProjectTemplateVersion(
                this.id, this.projectTemplateId, this.versionNumber,
                this.name, this.description,
                ProjectTemplateVersionStatus.PUBLISHED,
                Instant.now(), actorId,
                this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public ProjectTemplateVersion archive(UUID actorId) {
        return new ProjectTemplateVersion(
                this.id, this.projectTemplateId, this.versionNumber,
                this.name, this.description,
                ProjectTemplateVersionStatus.ARCHIVED,
                this.publishedAt, this.publishedBy,
                Instant.now(), actorId,
                this.version, this.createdAt, this.updatedAt
        );
    }
}
