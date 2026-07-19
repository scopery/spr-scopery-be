package com.company.scopery.modules.resourcecapacity.workload.domain.model;
import java.util.List; import java.util.UUID;
public interface WorkloadSnapshotRepository {
    WorkloadSnapshot save(WorkloadSnapshot s);
    List<WorkloadSnapshot> findByProjectId(UUID projectId);
    List<WorkloadSnapshot> findWithOverload();
}
