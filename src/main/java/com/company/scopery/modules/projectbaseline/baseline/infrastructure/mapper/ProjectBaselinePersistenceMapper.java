package com.company.scopery.modules.projectbaseline.baseline.infrastructure.mapper;

import com.company.scopery.modules.projectbaseline.baseline.domain.enums.BaselineStatus;
import com.company.scopery.modules.projectbaseline.baseline.domain.model.ProjectBaseline;
import com.company.scopery.modules.projectbaseline.baseline.infrastructure.persistence.ProjectBaselineJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectBaselinePersistenceMapper {
    public ProjectBaseline toDomain(ProjectBaselineJpaEntity e) {
        return new ProjectBaseline(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getBaselineNumber(), e.getName(),
                e.getDescription(), BaselineStatus.valueOf(e.getStatus()), e.isCurrentFlag(),
                e.getSourceScheduleRunId(), e.getSourceEstimationRunId(), e.getSourceFinanceScenarioId(),
                e.getSourceQuoteVersionId(), e.getSnapshotJson(), e.getSummaryJson(), e.getValidationJson(),
                e.getFormulaVersion(), e.getApprovedAt(), e.getApprovedBy(), e.getArchivedAt(), e.getArchivedBy(),
                e.getTraceId(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProjectBaselineJpaEntity toJpaEntity(ProjectBaseline d) {
        ProjectBaselineJpaEntity e = new ProjectBaselineJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setBaselineNumber(d.baselineNumber());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setStatus(d.status().name());
        e.setCurrentFlag(d.currentFlag());
        e.setSourceScheduleRunId(d.sourceScheduleRunId());
        e.setSourceEstimationRunId(d.sourceEstimationRunId());
        e.setSourceFinanceScenarioId(d.sourceFinanceScenarioId());
        e.setSourceQuoteVersionId(d.sourceQuoteVersionId());
        e.setSnapshotJson(d.snapshotJson());
        e.setSummaryJson(d.summaryJson());
        e.setValidationJson(d.validationJson());
        e.setFormulaVersion(d.formulaVersion());
        e.setApprovedAt(d.approvedAt());
        e.setApprovedBy(d.approvedBy());
        e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy());
        e.setTraceId(d.traceId());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
