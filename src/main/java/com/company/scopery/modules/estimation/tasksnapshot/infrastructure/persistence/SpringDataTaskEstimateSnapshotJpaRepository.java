package com.company.scopery.modules.estimation.tasksnapshot.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataTaskEstimateSnapshotJpaRepository extends JpaRepository<TaskEstimateSnapshotJpaEntity, UUID> {
    List<TaskEstimateSnapshotJpaEntity> findAllByEstimationRunIdOrderByTaskTitleAsc(UUID estimationRunId);
}
