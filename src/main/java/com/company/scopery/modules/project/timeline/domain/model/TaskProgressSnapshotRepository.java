package com.company.scopery.modules.project.timeline.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskProgressSnapshotRepository {
    TaskProgressSnapshot save(TaskProgressSnapshot snapshot);

    List<TaskProgressSnapshot> findByProjectId(UUID projectId);

    List<TaskProgressSnapshot> findByTaskId(UUID taskId);

    Optional<TaskProgressSnapshot> findByTaskIdAndSnapshotDate(UUID taskId, LocalDate snapshotDate);
}
