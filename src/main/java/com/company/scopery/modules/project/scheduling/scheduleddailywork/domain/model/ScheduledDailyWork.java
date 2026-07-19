package com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduledDailyWork(
        UUID id, UUID scheduleRunId, UUID taskScheduleId, UUID projectId, UUID taskId,
        UUID workspaceMemberId, UUID userId, LocalDate workDate, BigDecimal plannedHours,
        BigDecimal capacityHours, BigDecimal remainingCapacityAfter, Instant createdAt, Instant updatedAt) {

    public static ScheduledDailyWork create(UUID runId, UUID scheduleId, UUID projectId, UUID taskId,
                                            UUID memberId, UUID userId, LocalDate date, BigDecimal planned,
                                            BigDecimal capacity, BigDecimal remaining) {
        if (planned == null || planned.signum() <= 0) throw new IllegalArgumentException("plannedHours must be positive");
        return new ScheduledDailyWork(UUID.randomUUID(), runId, scheduleId, projectId, taskId, memberId,
                userId, date, planned, capacity, remaining, null, null);
    }
}
