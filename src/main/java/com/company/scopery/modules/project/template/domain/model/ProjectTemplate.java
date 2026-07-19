package com.company.scopery.modules.project.template.domain.model;

import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateCategory;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateScope;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateStatus;
import com.company.scopery.modules.project.template.domain.enums.ProjectTemplateVisibility;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplate(
        UUID id,
        String code,
        String name,
        String description,
        ProjectTemplateScope scope,
        UUID organizationId,
        UUID workspaceId,
        ProjectTemplateCategory category,
        ProjectTemplateVisibility visibility,
        ProjectTemplateStatus status,
        UUID currentVersionId,
        boolean builtIn,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectTemplate create(
            String code,
            String name,
            String description,
            ProjectTemplateScope scope,
            UUID organizationId,
            UUID workspaceId,
            ProjectTemplateCategory category,
            ProjectTemplateVisibility visibility,
            boolean builtIn) {
        return new ProjectTemplate(
                UUID.randomUUID(),
                code,
                name,
                description,
                scope,
                organizationId,
                workspaceId,
                category,
                visibility,
                ProjectTemplateStatus.DRAFT,
                null,
                builtIn,
                null,
                null,
                0,
                null,
                null
        );
    }

    public ProjectTemplate update(
            String name,
            String description,
            ProjectTemplateCategory category,
            ProjectTemplateVisibility visibility) {
        return new ProjectTemplate(
                this.id, this.code, name, description, this.scope,
                this.organizationId, this.workspaceId, category, visibility,
                this.status, this.currentVersionId, this.builtIn,
                this.archivedAt, this.archivedBy, this.version,
                this.createdAt, this.updatedAt
        );
    }

    public ProjectTemplate activate(UUID currentVersionId) {
        return new ProjectTemplate(
                this.id, this.code, this.name, this.description, this.scope,
                this.organizationId, this.workspaceId, this.category, this.visibility,
                ProjectTemplateStatus.ACTIVE, currentVersionId, this.builtIn,
                this.archivedAt, this.archivedBy, this.version,
                this.createdAt, this.updatedAt
        );
    }

    public ProjectTemplate deactivate() {
        return new ProjectTemplate(
                this.id, this.code, this.name, this.description, this.scope,
                this.organizationId, this.workspaceId, this.category, this.visibility,
                ProjectTemplateStatus.INACTIVE, this.currentVersionId, this.builtIn,
                this.archivedAt, this.archivedBy, this.version,
                this.createdAt, this.updatedAt
        );
    }

    public ProjectTemplate archive(UUID actorId) {
        return new ProjectTemplate(
                this.id, this.code, this.name, this.description, this.scope,
                this.organizationId, this.workspaceId, this.category, this.visibility,
                ProjectTemplateStatus.ARCHIVED, this.currentVersionId, this.builtIn,
                Instant.now(), actorId, this.version,
                this.createdAt, this.updatedAt
        );
    }

    public ProjectTemplate withCurrentVersionId(UUID currentVersionId) {
        return new ProjectTemplate(
                this.id, this.code, this.name, this.description, this.scope,
                this.organizationId, this.workspaceId, this.category, this.visibility,
                this.status, currentVersionId, this.builtIn,
                this.archivedAt, this.archivedBy, this.version,
                this.createdAt, this.updatedAt
        );
    }
}
