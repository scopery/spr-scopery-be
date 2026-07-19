package com.company.scopery.modules.estimation.tasksnapshot.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskEstimateSnapshotRepository {
    TaskEstimateSnapshot save(TaskEstimateSnapshot snapshot);
    List<TaskEstimateSnapshot> saveAll(List<TaskEstimateSnapshot> snapshots);
    Optional<TaskEstimateSnapshot> findById(UUID id);
    List<TaskEstimateSnapshot> findAllByEstimationRunId(UUID estimationRunId);
}
