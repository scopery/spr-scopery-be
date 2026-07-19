package com.company.scopery.modules.servicesupport.snapshot.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.snapshot.domain.model.SupportMetricSnapshot;
import com.company.scopery.modules.servicesupport.snapshot.infrastructure.persistence.SupportMetricSnapshotJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportMetricSnapshotPersistenceMapper {
    public SupportMetricSnapshotJpaEntity toJpa(SupportMetricSnapshot d) {
        var e = new SupportMetricSnapshotJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setServiceProfileId(d.serviceProfileId()); e.setPeriodStart(d.periodStart()); e.setPeriodEnd(d.periodEnd());
        e.setOpenCases(d.openCases()); e.setNewCases(d.newCases()); e.setResolvedCases(d.resolvedCases());
        e.setClosedCases(d.closedCases()); e.setSlaAtRisk(d.slaAtRisk()); e.setSlaBreached(d.slaBreached());
        e.setCriticalIncidents(d.criticalIncidents()); e.setAvgFirstResponseMinutes(d.avgFirstResponseMinutes());
        e.setAvgResolutionMinutes(d.avgResolutionMinutes()); e.setMaintenanceWindowsPlanned(d.maintenanceWindowsPlanned());
        e.setSupportEffortHours(d.supportEffortHours()); e.setSupportCostInput(d.supportCostInput());
        e.setCurrency(d.currency()); e.setSnapshotSource(d.snapshotSource()); e.setSnapshotAt(d.snapshotAt());
        e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportMetricSnapshot toDomain(SupportMetricSnapshotJpaEntity e) {
        return new SupportMetricSnapshot(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getServiceProfileId(),
                e.getPeriodStart(), e.getPeriodEnd(), e.getOpenCases(), e.getNewCases(), e.getResolvedCases(),
                e.getClosedCases(), e.getSlaAtRisk(), e.getSlaBreached(), e.getCriticalIncidents(),
                e.getAvgFirstResponseMinutes(), e.getAvgResolutionMinutes(), e.getMaintenanceWindowsPlanned(),
                e.getSupportEffortHours(), e.getSupportCostInput(), e.getCurrency(), e.getSnapshotSource(),
                e.getSnapshotAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
