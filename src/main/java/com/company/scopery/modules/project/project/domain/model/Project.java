package com.company.scopery.modules.project.project.domain.model;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Project(
        UUID id,
        UUID workspaceId,
        String code,
        String name,
        String description,
        UUID ownerUserId,
        String defaultCurrency,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        ProjectStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static Project create(
            UUID workspaceId,
            String code,
            String name,
            String description,
            UUID ownerUserId,
            String defaultCurrency,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new Project(
                UUID.randomUUID(),
                workspaceId,
                code,
                name,
                description,
                ownerUserId,
                defaultCurrency,
                plannedStartDate,
                plannedEndDate,
                ProjectStatus.DRAFT,
                0,
                null,
                null
        );
    }

    public Project update(
            String name,
            String description,
            UUID ownerUserId,
            String defaultCurrency,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new Project(
                this.id,
                this.workspaceId,
                this.code,
                name,
                description,
                ownerUserId,
                defaultCurrency,
                plannedStartDate,
                plannedEndDate,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public Project activate() {
        return new Project(
                this.id, this.workspaceId, this.code, this.name, this.description,
                this.ownerUserId, this.defaultCurrency, this.plannedStartDate, this.plannedEndDate,
                ProjectStatus.ACTIVE, this.version, this.createdAt, this.updatedAt
        );
    }

    public Project hold() {
        return new Project(
                this.id, this.workspaceId, this.code, this.name, this.description,
                this.ownerUserId, this.defaultCurrency, this.plannedStartDate, this.plannedEndDate,
                ProjectStatus.ON_HOLD, this.version, this.createdAt, this.updatedAt
        );
    }

    public Project complete() {
        return new Project(
                this.id, this.workspaceId, this.code, this.name, this.description,
                this.ownerUserId, this.defaultCurrency, this.plannedStartDate, this.plannedEndDate,
                ProjectStatus.COMPLETED, this.version, this.createdAt, this.updatedAt
        );
    }

    public Project archive() {
        return new Project(
                this.id, this.workspaceId, this.code, this.name, this.description,
                this.ownerUserId, this.defaultCurrency, this.plannedStartDate, this.plannedEndDate,
                ProjectStatus.ARCHIVED, this.version, this.createdAt, this.updatedAt
        );
    }
}
