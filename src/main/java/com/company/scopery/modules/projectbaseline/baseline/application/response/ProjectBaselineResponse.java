package com.company.scopery.modules.projectbaseline.baseline.application.response;

import com.company.scopery.modules.projectbaseline.baseline.application.service.BaselineSnapshotParser;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProjectBaselineResponse(
        UUID id, UUID projectId, UUID workspaceId, int baselineNumber, String name, String description,
        String status, boolean currentFlag, String formulaVersion,
        UUID sourceScheduleRunId, UUID sourceEstimationRunId, UUID sourceFinanceScenarioId, UUID sourceQuoteVersionId,
        Instant approvedAt, UUID approvedBy, Instant archivedAt, UUID archivedBy,
        Instant createdAt, Instant updatedAt,
        BaselineSummaryDto summary,
        List<BaselineTreeNodeDto> projectTree,
        BaselineHealthDto health,
        BaselineProvenanceDto provenance
) {
    public static ProjectBaselineResponse from(ProjectBaseline b, BaselineSnapshotParser parser) {
        return new ProjectBaselineResponse(
                b.id(), b.projectId(), b.workspaceId(), b.baselineNumber(), b.name(), b.description(),
                b.status().name(), b.currentFlag(), b.formulaVersion(),
                b.sourceScheduleRunId(), b.sourceEstimationRunId(), b.sourceFinanceScenarioId(), b.sourceQuoteVersionId(),
                b.approvedAt(), b.approvedBy(), b.archivedAt(), b.archivedBy(),
                b.createdAt(), b.updatedAt(),
                parser.parseSummary(b.summaryJson(), b.snapshotJson()),
                parser.parseProjectTree(b.snapshotJson()),
                parser.parseHealth(b.summaryJson(), b.validationJson(), b.status().name(), b.approvedAt(), b.approvedBy()),
                parser.parseProvenance(b.snapshotJson(), b.createdAt())
        );
    }
}
