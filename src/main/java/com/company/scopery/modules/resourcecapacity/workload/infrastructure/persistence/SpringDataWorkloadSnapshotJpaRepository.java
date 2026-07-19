package com.company.scopery.modules.resourcecapacity.workload.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataWorkloadSnapshotJpaRepository extends JpaRepository<WorkloadSnapshotJpaEntity, UUID> {
    List<WorkloadSnapshotJpaEntity> findByProjectIdOrderBySnapshotAtDesc(UUID projectId);
    List<WorkloadSnapshotJpaEntity> findByOverloadCountGreaterThanOrderBySnapshotAtDesc(int min);
}
