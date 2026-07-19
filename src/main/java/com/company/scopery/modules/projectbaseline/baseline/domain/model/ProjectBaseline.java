package com.company.scopery.modules.projectbaseline.baseline.domain.model;

import com.company.scopery.modules.projectbaseline.baseline.domain.enums.BaselineStatus;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineFormulaVersions;

import java.time.Instant;
import java.util.UUID;

public record ProjectBaseline(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        int baselineNumber,
        String name,
        String description,
        BaselineStatus status,
        boolean currentFlag,
        UUID sourceScheduleRunId,
        UUID sourceEstimationRunId,
        UUID sourceFinanceScenarioId,
        UUID sourceQuoteVersionId,
        String snapshotJson,
        String summaryJson,
        String validationJson,
        String formulaVersion,
        Instant approvedAt,
        UUID approvedBy,
        Instant archivedAt,
        UUID archivedBy,
        String traceId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectBaseline create(
            UUID projectId, UUID workspaceId, int baselineNumber, String name, String description,
            UUID sourceScheduleRunId, UUID sourceEstimationRunId, UUID sourceFinanceScenarioId,
            UUID sourceQuoteVersionId, String snapshotJson, String summaryJson, String traceId) {
        return new ProjectBaseline(
                UUID.randomUUID(), projectId, workspaceId, baselineNumber, name, description,
                BaselineStatus.DRAFT, false,
                sourceScheduleRunId, sourceEstimationRunId, sourceFinanceScenarioId, sourceQuoteVersionId,
                snapshotJson, summaryJson, null, ProjectBaselineFormulaVersions.BASELINE_V1,
                null, null, null, null, traceId, 0, null, null);
    }

    public boolean isDraft() { return status == BaselineStatus.DRAFT; }
    public boolean isEditable() { return status == BaselineStatus.DRAFT; }
    public boolean isImmutable() {
        return status == BaselineStatus.APPROVED || (status == BaselineStatus.ARCHIVED && approvedAt != null);
    }

    public ProjectBaseline updateDraft(String name, String description) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description, status,
                currentFlag, sourceScheduleRunId, sourceEstimationRunId, sourceFinanceScenarioId,
                sourceQuoteVersionId, snapshotJson, summaryJson, validationJson, formulaVersion,
                approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ProjectBaseline withSnapshot(String snapshotJson, String summaryJson) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description, status,
                currentFlag, sourceScheduleRunId, sourceEstimationRunId, sourceFinanceScenarioId,
                sourceQuoteVersionId, snapshotJson, summaryJson, validationJson, formulaVersion,
                approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ProjectBaseline withSources(UUID scheduleRunId, UUID estimationRunId, UUID financeScenarioId, UUID quoteVersionId) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description, status,
                currentFlag, scheduleRunId, estimationRunId, financeScenarioId, quoteVersionId,
                snapshotJson, summaryJson, validationJson, formulaVersion,
                approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ProjectBaseline markReady(String validationJson) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description,
                BaselineStatus.READY, currentFlag, sourceScheduleRunId, sourceEstimationRunId,
                sourceFinanceScenarioId, sourceQuoteVersionId, snapshotJson, summaryJson, validationJson,
                formulaVersion, approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ProjectBaseline withValidation(String validationJson) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description, status,
                currentFlag, sourceScheduleRunId, sourceEstimationRunId, sourceFinanceScenarioId,
                sourceQuoteVersionId, snapshotJson, summaryJson, validationJson, formulaVersion,
                approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ProjectBaseline approve(UUID actorId) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description,
                BaselineStatus.APPROVED, currentFlag, sourceScheduleRunId, sourceEstimationRunId,
                sourceFinanceScenarioId, sourceQuoteVersionId, snapshotJson, summaryJson, validationJson,
                formulaVersion, Instant.now(), actorId, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ProjectBaseline withCurrentFlag(boolean currentFlag) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description, status,
                currentFlag, sourceScheduleRunId, sourceEstimationRunId, sourceFinanceScenarioId,
                sourceQuoteVersionId, snapshotJson, summaryJson, validationJson, formulaVersion,
                approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ProjectBaseline archive(UUID actorId) {
        return new ProjectBaseline(id, projectId, workspaceId, baselineNumber, name, description,
                BaselineStatus.ARCHIVED, false, sourceScheduleRunId, sourceEstimationRunId,
                sourceFinanceScenarioId, sourceQuoteVersionId, snapshotJson, summaryJson, validationJson,
                formulaVersion, approvedAt, approvedBy, Instant.now(), actorId, traceId, version, createdAt, updatedAt);
    }
}
