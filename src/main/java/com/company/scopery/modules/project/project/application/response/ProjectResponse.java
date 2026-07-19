package com.company.scopery.modules.project.project.application.response;

import com.company.scopery.modules.project.project.domain.model.Project;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID workspaceId,
        UUID organizationId,
        String code,
        String name,
        String description,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        String status,
        Instant activatedAt,
        UUID activatedBy,
        Instant archivedAt,
        UUID archivedBy,
        UUID sourceTemplateId,
        UUID sourceTemplateVersionId,
        Instant sourceTemplateAppliedAt,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectResponse from(Project p) {
        return new ProjectResponse(
                p.id(),
                p.workspaceId(),
                p.organizationId(),
                p.code(),
                p.name(),
                p.description(),
                p.ownerUserId(),
                p.defaultCurrency(),
                p.plannedStartDate(),
                p.plannedEndDate(),
                p.status().name(),
                p.activatedAt(),
                p.activatedBy(),
                p.archivedAt(),
                p.archivedBy(),
                p.sourceTemplateId(),
                p.sourceTemplateVersionId(),
                p.sourceTemplateAppliedAt(),
                p.version(),
                p.createdAt(),
                p.updatedAt()
        );
    }
}
