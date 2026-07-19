package com.company.scopery.modules.project.scheduling.taskschedule.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskScheduleRepository {
    List<TaskSchedule> saveAll(List<TaskSchedule> schedules);
    Optional<TaskSchedule> findByScheduleRunIdAndTaskId(UUID runId, UUID taskId);
    List<TaskSchedule> findAllByScheduleRunId(UUID runId);
    List<TaskSchedule> findHistory(UUID projectId, UUID taskId);
}
