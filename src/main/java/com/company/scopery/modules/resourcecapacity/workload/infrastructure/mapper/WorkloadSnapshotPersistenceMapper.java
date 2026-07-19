package com.company.scopery.modules.resourcecapacity.workload.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshot;
import com.company.scopery.modules.resourcecapacity.workload.infrastructure.persistence.WorkloadSnapshotJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class WorkloadSnapshotPersistenceMapper {
    public WorkloadSnapshotJpaEntity toJpaEntity(WorkloadSnapshot d) {
        WorkloadSnapshotJpaEntity e = new WorkloadSnapshotJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setTotalCapacityHours(d.totalCapacityHours()); e.setTotalAllocatedHours(d.totalAllocatedHours());
        e.setTotalEstimatedEffortHours(d.totalEstimatedEffortHours()); e.setTotalForecastEffortHours(d.totalForecastEffortHours());
        e.setTotalActualObservedEffortHours(d.totalActualObservedEffortHours()); e.setCapacityGapHours(d.capacityGapHours());
        e.setOverloadCount(d.overloadCount()); e.setUnderstaffedRoleCount(d.understaffedRoleCount());
        e.setCostForecastInput(d.costForecastInput()); e.setSnapshotSource(d.snapshotSource()); e.setSnapshotAt(d.snapshotAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public WorkloadSnapshot toDomain(WorkloadSnapshotJpaEntity e) {
        return new WorkloadSnapshot(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getTotalCapacityHours(), e.getTotalAllocatedHours(),
                e.getTotalEstimatedEffortHours(), e.getTotalForecastEffortHours(), e.getTotalActualObservedEffortHours(),
                e.getCapacityGapHours(), e.getOverloadCount(), e.getUnderstaffedRoleCount(), e.getCostForecastInput(),
                e.getSnapshotSource(), e.getSnapshotAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
}
