package com.company.scopery.modules.project.scheduleoverride.application.response;

import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskScheduleOverrideResponse(
        UUID id,
        UUID projectId,
        UUID taskId,
        String overrideType,
        LocalDate manualStartDate,
        LocalDate manualFinishDate,
        LocalDate manualDueDate,
        String reason,
        String status,
        Instant cancelledAt,
        UUID cancelledBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskScheduleOverrideResponse from(TaskScheduleOverride o) {
        return new TaskScheduleOverrideResponse(
                o.id(), o.projectId(), o.taskId(), o.overrideType().name(),
                o.manualStartDate(), o.manualFinishDate(), o.manualDueDate(),
                o.reason(), o.status().name(),
                o.cancelledAt(), o.cancelledBy(),
                o.version(), o.createdAt(), o.updatedAt());
    }
}
