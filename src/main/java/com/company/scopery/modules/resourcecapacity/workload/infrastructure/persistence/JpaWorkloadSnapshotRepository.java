package com.company.scopery.modules.resourcecapacity.workload.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.*;
import com.company.scopery.modules.resourcecapacity.workload.infrastructure.mapper.WorkloadSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaWorkloadSnapshotRepository implements WorkloadSnapshotRepository {
    private final SpringDataWorkloadSnapshotJpaRepository spring; private final WorkloadSnapshotPersistenceMapper mapper;
    public JpaWorkloadSnapshotRepository(SpringDataWorkloadSnapshotJpaRepository spring, WorkloadSnapshotPersistenceMapper mapper) {
        this.spring=spring; this.mapper=mapper;
    }
    @Override public WorkloadSnapshot save(WorkloadSnapshot s) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(s))); }
    @Override public List<WorkloadSnapshot> findByProjectId(UUID projectId) {
        return spring.findByProjectIdOrderBySnapshotAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<WorkloadSnapshot> findWithOverload() {
        return spring.findByOverloadCountGreaterThanOrderBySnapshotAtDesc(0).stream().map(mapper::toDomain).toList();
    }
}
