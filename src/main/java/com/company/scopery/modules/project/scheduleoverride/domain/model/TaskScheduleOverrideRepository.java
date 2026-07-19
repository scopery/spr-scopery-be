package com.company.scopery.modules.project.scheduleoverride.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskScheduleOverrideRepository {
    TaskScheduleOverride save(TaskScheduleOverride override);
    Optional<TaskScheduleOverride> findById(UUID id);
    Optional<TaskScheduleOverride> findActiveByTaskId(UUID taskId);
    List<TaskScheduleOverride> findActiveByProjectId(UUID projectId);
}
