package com.company.scopery.modules.project.scheduleoverride.domain.model;

import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideStatus;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Model B — manual schedule override for Gantt move/resize.
 * ScheduleEngineService must respect ACTIVE overrides.
 */
public record TaskScheduleOverride(
        UUID id,
        UUID projectId,
        UUID taskId,
        ScheduleOverrideType overrideType,
        LocalDate manualStartDate,
        LocalDate manualFinishDate,
        LocalDate manualDueDate,
        String reason,
        ScheduleOverrideStatus status,
        Instant cancelledAt,
        UUID cancelledBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static TaskScheduleOverride create(
            UUID projectId,
            UUID taskId,
            ScheduleOverrideType overrideType,
            LocalDate manualStartDate,
            LocalDate manualFinishDate,
            LocalDate manualDueDate,
            String reason) {
        return new TaskScheduleOverride(
                UUID.randomUUID(),
                projectId,
                taskId,
                overrideType,
                manualStartDate,
                manualFinishDate,
                manualDueDate,
                reason,
                ScheduleOverrideStatus.ACTIVE,
                null,
                null,
                0,
                null,
                null
        );
    }

    public TaskScheduleOverride cancel(UUID actorId) {
        return new TaskScheduleOverride(
                this.id, this.projectId, this.taskId, this.overrideType,
                this.manualStartDate, this.manualFinishDate, this.manualDueDate,
                this.reason, ScheduleOverrideStatus.CANCELLED,
                Instant.now(), actorId,
                this.version, this.createdAt, this.updatedAt
        );
    }
}
