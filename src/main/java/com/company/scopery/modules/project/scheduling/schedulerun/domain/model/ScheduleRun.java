package com.company.scopery.modules.project.scheduling.schedulerun.domain.model;

import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduleRun(
        UUID id, UUID projectId, UUID workspaceId, ScheduleRunStatus status,
        String algorithmVersion, LocalDate planningStartDate, LocalDate planningEndDate,
        String inputSummaryJson, String resultSummaryJson, String errorCode, String errorMessage,
        Instant startedAt, Instant completedAt, UUID actorUserId, String traceId,
        int version, Instant createdAt, Instant updatedAt) {

    public static ScheduleRun create(UUID projectId, UUID workspaceId, LocalDate start, LocalDate end,
                                     String input, UUID actorUserId, String traceId) {
        return new ScheduleRun(UUID.randomUUID(), projectId, workspaceId, ScheduleRunStatus.PENDING,
                "1.0.0", start, end, input, null, null, null, null, null, actorUserId, traceId, 0, null, null);
    }

    public ScheduleRun running() {
        return copy(ScheduleRunStatus.RUNNING, resultSummaryJson, null, null, Instant.now(), null);
    }

    public ScheduleRun completed(String summary) {
        return copy(ScheduleRunStatus.COMPLETED, summary, null, null, startedAt, Instant.now());
    }

    public ScheduleRun failed(String code, String message) {
        return copy(ScheduleRunStatus.FAILED, resultSummaryJson, code, message, startedAt, Instant.now());
    }

    public ScheduleRun cancelled() {
        return copy(ScheduleRunStatus.CANCELLED, resultSummaryJson, null, null, startedAt, Instant.now());
    }

    public boolean cancellable() {
        return status == ScheduleRunStatus.PENDING || status == ScheduleRunStatus.RUNNING;
    }

    private ScheduleRun copy(ScheduleRunStatus newStatus, String result, String code, String message,
                             Instant started, Instant completed) {
        return new ScheduleRun(id, projectId, workspaceId, newStatus, algorithmVersion, planningStartDate,
                planningEndDate, inputSummaryJson, result, code, message, started, completed, actorUserId,
                traceId, version, createdAt, updatedAt);
    }
}
