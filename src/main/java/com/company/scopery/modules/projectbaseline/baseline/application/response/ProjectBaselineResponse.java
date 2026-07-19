package com.company.scopery.modules.projectbaseline.baseline.application.response;

import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;

import java.time.Instant;
import java.util.UUID;

public record ProjectBaselineResponse(
        UUID id, UUID projectId, UUID workspaceId, int baselineNumber, String name, String description,
        String status, boolean currentFlag,
        UUID sourceScheduleRunId, UUID sourceEstimationRunId, UUID sourceFinanceScenarioId, UUID sourceQuoteVersionId,
        String snapshotJson, String summaryJson, String validationJson, String formulaVersion,
        Instant approvedAt, UUID approvedBy, Instant archivedAt, UUID archivedBy,
        Instant createdAt, Instant updatedAt
) {
    public static ProjectBaselineResponse from(ProjectBaseline b) {
        return new ProjectBaselineResponse(
                b.id(), b.projectId(), b.workspaceId(), b.baselineNumber(), b.name(), b.description(),
                b.status().name(), b.currentFlag(),
                b.sourceScheduleRunId(), b.sourceEstimationRunId(), b.sourceFinanceScenarioId(), b.sourceQuoteVersionId(),
                b.snapshotJson(), b.summaryJson(), b.validationJson(), b.formulaVersion(),
                b.approvedAt(), b.approvedBy(), b.archivedAt(), b.archivedBy(),
                b.createdAt(), b.updatedAt());
    }
}
