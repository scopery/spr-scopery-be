package com.company.scopery.modules.reporting.run.infrastructure.mapper;
import com.company.scopery.modules.reporting.run.domain.enums.ReportRunStatus;
import com.company.scopery.modules.reporting.run.domain.model.ReportRun;
import com.company.scopery.modules.reporting.run.infrastructure.persistence.ReportRunJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ReportRunPersistenceMapper {
    public ReportRun toDomain(ReportRunJpaEntity e) {
        return new ReportRun(e.getId(), e.getReportDefinitionId(), e.getWorkspaceId(), e.getProjectId(), e.getActorUserId(),
                ReportRunStatus.valueOf(e.getStatus()), e.getFiltersJson(), e.getSelectedFieldsJson(), e.getAccessSummaryJson(),
                e.getMaskingSummaryJson(), e.getResultSummaryJson(), e.getErrorCode(), e.getErrorMessage(),
                e.getStartedAt(), e.getCompletedAt(), e.getTraceId(), e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public ReportRunJpaEntity toJpaEntity(ReportRun d) {
        ReportRunJpaEntity e = new ReportRunJpaEntity();
        e.setId(d.id()); e.setReportDefinitionId(d.reportDefinitionId()); e.setWorkspaceId(d.workspaceId());
        e.setProjectId(d.projectId()); e.setActorUserId(d.actorUserId()); e.setStatus(d.status().name());
        e.setFiltersJson(d.filtersJson()); e.setSelectedFieldsJson(d.selectedFieldsJson());
        e.setAccessSummaryJson(d.accessSummaryJson()); e.setMaskingSummaryJson(d.maskingSummaryJson());
        e.setResultSummaryJson(d.resultSummaryJson()); e.setErrorCode(d.errorCode()); e.setErrorMessage(d.errorMessage());
        e.setStartedAt(d.startedAt()); e.setCompletedAt(d.completedAt()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
