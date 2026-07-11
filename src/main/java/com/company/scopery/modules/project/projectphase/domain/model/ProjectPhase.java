package com.company.scopery.modules.project.projectphase.domain.model;

import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectPhase(
        UUID id,
        UUID projectId,
        UUID phaseDefinitionId,
        String code,
        String name,
        String description,
        int displayOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        ProjectPhaseStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectPhase create(
            UUID projectId,
            String code,
            String name,
            String description,
            int displayOrder,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new ProjectPhase(
                UUID.randomUUID(),
                projectId,
                null,
                code,
                name,
                description,
                displayOrder,
                plannedStartDate,
                plannedEndDate,
                ProjectPhaseStatus.PLANNED,
                0,
                null,
                null
        );
    }

    public static ProjectPhase createFromDefinition(
            UUID projectId,
            UUID phaseDefinitionId,
            String code,
            String name,
            String description,
            int displayOrder,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new ProjectPhase(
                UUID.randomUUID(),
                projectId,
                phaseDefinitionId,
                code,
                name,
                description,
                displayOrder,
                plannedStartDate,
                plannedEndDate,
                ProjectPhaseStatus.PLANNED,
                0,
                null,
                null
        );
    }

    public ProjectPhase update(
            String name,
            String description,
            int displayOrder,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new ProjectPhase(
                this.id,
                this.projectId,
                this.phaseDefinitionId,
                this.code,
                name,
                description,
                displayOrder,
                plannedStartDate,
                plannedEndDate,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public ProjectPhase activate() {
        return new ProjectPhase(
                this.id, this.projectId, this.phaseDefinitionId, this.code, this.name,
                this.description, this.displayOrder, this.plannedStartDate, this.plannedEndDate,
                ProjectPhaseStatus.ACTIVE, this.version, this.createdAt, this.updatedAt
        );
    }

    public ProjectPhase complete() {
        return new ProjectPhase(
                this.id, this.projectId, this.phaseDefinitionId, this.code, this.name,
                this.description, this.displayOrder, this.plannedStartDate, this.plannedEndDate,
                ProjectPhaseStatus.COMPLETED, this.version, this.createdAt, this.updatedAt
        );
    }

    public ProjectPhase archive() {
        return new ProjectPhase(
                this.id, this.projectId, this.phaseDefinitionId, this.code, this.name,
                this.description, this.displayOrder, this.plannedStartDate, this.plannedEndDate,
                ProjectPhaseStatus.ARCHIVED, this.version, this.createdAt, this.updatedAt
        );
    }
}
