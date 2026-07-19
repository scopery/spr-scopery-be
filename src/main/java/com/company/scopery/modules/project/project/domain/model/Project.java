package com.company.scopery.modules.project.project.domain.model;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Project(
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
        ProjectStatus status,
        Instant activatedAt,
        UUID activatedBy,
        Instant archivedAt,
        UUID archivedBy,
        UUID sourceTemplateId,
        UUID sourceTemplateVersionId,
        Instant sourceTemplateAppliedAt,
        UUID currentScheduleRunId,
        UUID currentEstimationRunId,
        UUID currentFinanceScenarioId,
        UUID currentQuoteId,
        UUID currentQuoteVersionId,
        UUID currentBaselineId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static Project create(
            UUID workspaceId,
            UUID organizationId,
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
                organizationId,
                code,
                name,
                description,
                ownerUserId,
                defaultCurrency,
                plannedStartDate,
                plannedEndDate,
                ProjectStatus.DRAFT,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
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
        return copy(
                this.status, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                name, description, ownerUserId, defaultCurrency, plannedStartDate, plannedEndDate);
    }

    public Project withSourceTemplate(UUID templateId, UUID versionId, Instant appliedAt) {
        return copy(
                this.status, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                templateId, versionId, appliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project activate(UUID actorId) {
        return copy(
                ProjectStatus.ACTIVE, Instant.now(), actorId, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project hold() {
        return copy(
                ProjectStatus.ON_HOLD, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project complete() {
        return copy(
                ProjectStatus.COMPLETED, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project archive(UUID actorId) {
        return copy(
                ProjectStatus.ARCHIVED, this.activatedAt, this.activatedBy, Instant.now(), actorId,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project withCurrentScheduleRunId(UUID scheduleRunId) {
        return copy(
                this.status, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                scheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project withCurrentEstimationRunId(UUID estimationRunId) {
        return copy(
                this.status, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, estimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project withCurrentFinanceScenarioId(UUID financeScenarioId) {
        return copy(
                this.status, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, financeScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project withCurrentQuoteIds(UUID quoteId, UUID quoteVersionId) {
        return copy(
                this.status, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                quoteId, quoteVersionId, this.currentBaselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    public Project withCurrentBaselineId(UUID baselineId) {
        return copy(
                this.status, this.activatedAt, this.activatedBy, this.archivedAt, this.archivedBy,
                this.sourceTemplateId, this.sourceTemplateVersionId, this.sourceTemplateAppliedAt,
                this.currentScheduleRunId, this.currentEstimationRunId, this.currentFinanceScenarioId,
                this.currentQuoteId, this.currentQuoteVersionId, baselineId,
                this.name, this.description, this.ownerUserId, this.defaultCurrency,
                this.plannedStartDate, this.plannedEndDate);
    }

    private Project copy(
            ProjectStatus status,
            Instant activatedAt,
            UUID activatedBy,
            Instant archivedAt,
            UUID archivedBy,
            UUID sourceTemplateId,
            UUID sourceTemplateVersionId,
            Instant sourceTemplateAppliedAt,
            UUID currentScheduleRunId,
            UUID currentEstimationRunId,
            UUID currentFinanceScenarioId,
            UUID currentQuoteId,
            UUID currentQuoteVersionId,
            UUID currentBaselineId,
            String name,
            String description,
            UUID ownerUserId,
            String defaultCurrency,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new Project(
                this.id,
                this.workspaceId,
                this.organizationId,
                this.code,
                name,
                description,
                ownerUserId,
                defaultCurrency,
                plannedStartDate,
                plannedEndDate,
                status,
                activatedAt,
                activatedBy,
                archivedAt,
                archivedBy,
                sourceTemplateId,
                sourceTemplateVersionId,
                sourceTemplateAppliedAt,
                currentScheduleRunId,
                currentEstimationRunId,
                currentFinanceScenarioId,
                currentQuoteId,
                currentQuoteVersionId,
                currentBaselineId,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }
}
