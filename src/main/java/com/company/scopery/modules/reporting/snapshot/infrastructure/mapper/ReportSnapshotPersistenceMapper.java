package com.company.scopery.modules.reporting.snapshot.infrastructure.mapper;
import com.company.scopery.modules.reporting.snapshot.domain.model.ReportSnapshot;
import com.company.scopery.modules.reporting.snapshot.infrastructure.persistence.ReportSnapshotJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ReportSnapshotPersistenceMapper {
    public ReportSnapshot toDomain(ReportSnapshotJpaEntity e) {
        return new ReportSnapshot(e.getId(), e.getReportRunId(), e.getReportDefinitionId(), e.getWorkspaceId(), e.getProjectId(),
                e.getActorUserId(), e.getSnapshotType(), e.getDataJson(), e.getSummaryJson(), e.getMaskingSummaryJson(),
                e.getGeneratedAt(), e.getCreatedAt(), e.getVersion() == null ? 0 : e.getVersion());
    }
    public ReportSnapshotJpaEntity toJpaEntity(ReportSnapshot d) {
        ReportSnapshotJpaEntity e = new ReportSnapshotJpaEntity();
        e.setId(d.id()); e.setReportRunId(d.reportRunId()); e.setReportDefinitionId(d.reportDefinitionId());
        e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setActorUserId(d.actorUserId());
        e.setSnapshotType(d.snapshotType()); e.setDataJson(d.dataJson()); e.setSummaryJson(d.summaryJson());
        e.setMaskingSummaryJson(d.maskingSummaryJson()); e.setGeneratedAt(d.generatedAt()); e.setCreatedAt(d.createdAt());
        e.setVersion(d.version());
        return e;
    }
}
