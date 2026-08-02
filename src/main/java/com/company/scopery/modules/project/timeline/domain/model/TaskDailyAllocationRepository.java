package com.company.scopery.modules.project.timeline.domain.model;

import java.util.List;
import java.util.UUID;

public interface TaskDailyAllocationRepository {
    TaskDailyAllocation save(TaskDailyAllocation allocation);

    List<TaskDailyAllocation> saveAll(List<TaskDailyAllocation> allocations);

    List<TaskDailyAllocation> findByProjectId(UUID projectId);

    List<TaskDailyAllocation> findByTaskId(UUID taskId);

    void deleteManualByTaskId(UUID taskId);
}
