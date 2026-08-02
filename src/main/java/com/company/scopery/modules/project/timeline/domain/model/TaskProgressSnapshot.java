package com.company.scopery.modules.project.timeline.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskProgressSnapshot(
        UUID id,
        UUID projectId,
        UUID taskId,
        LocalDate snapshotDate,
        BigDecimal progressPercent,
        Integer timeSpentMinutes,
        String note,
        UUID recordedBy,
        Instant recordedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskProgressSnapshot create(
            UUID projectId,
            UUID taskId,
            LocalDate snapshotDate,
            BigDecimal progressPercent,
            Integer timeSpentMinutes,
            String note,
            UUID recordedBy) {
        Instant now = Instant.now();
        return new TaskProgressSnapshot(
                UUID.randomUUID(),
                projectId,
                taskId,
                snapshotDate,
                progressPercent,
                timeSpentMinutes,
                note,
                recordedBy,
                now,
                null,
                null);
    }

    public TaskProgressSnapshot withProgress(
            BigDecimal progressPercent,
            Integer timeSpentMinutes,
            String note,
            UUID recordedBy) {
        return new TaskProgressSnapshot(
                id,
                projectId,
                taskId,
                snapshotDate,
                progressPercent,
                timeSpentMinutes,
                note,
                recordedBy,
                Instant.now(),
                createdAt,
                updatedAt);
    }
}
