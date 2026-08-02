package com.company.scopery.modules.project.timeline.domain.model;

import com.company.scopery.modules.project.timeline.domain.enums.AllocationSource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskDailyAllocation(
        UUID id,
        UUID projectId,
        UUID taskId,
        LocalDate workDate,
        int plannedMinutes,
        AllocationSource source,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskDailyAllocation create(
            UUID projectId,
            UUID taskId,
            LocalDate workDate,
            int plannedMinutes,
            AllocationSource source) {
        return new TaskDailyAllocation(
                UUID.randomUUID(), projectId, taskId, workDate, plannedMinutes, source, null, null);
    }
}
