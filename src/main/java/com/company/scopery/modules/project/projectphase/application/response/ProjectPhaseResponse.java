package com.company.scopery.modules.project.projectphase.application.response;

import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectPhaseResponse(
        UUID id,
        UUID projectId,
        UUID phaseDefinitionId,
        String code,
        String name,
        String description,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectPhaseResponse from(ProjectPhase phase) {
        return new ProjectPhaseResponse(
                phase.id(),
                phase.projectId(),
                phase.phaseDefinitionId(),
                phase.code(),
                phase.name(),
                phase.description(),
                phase.displayOrder(),
                phase.plannedStartDate(),
                phase.plannedEndDate(),
                phase.status().name(),
                phase.version(),
                phase.createdAt(),
                phase.updatedAt()
        );
    }
}
