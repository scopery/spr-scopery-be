package com.company.scopery.modules.projectnotification.reminder.domain.model;

import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderRunStatus;
import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderRunType;

import java.time.Instant;
import java.util.UUID;

public record ProjectReminderRun(
        UUID id,
        UUID workspaceId,
        ReminderRunType runType,
        ReminderRunStatus status,
        Instant startedAt,
        Instant completedAt,
        String resultSummaryJson,
        String errorCode,
        String errorMessage,
        String traceId,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectReminderRun start(UUID workspaceId, ReminderRunType runType, String traceId) {
        Instant now = Instant.now();
        return new ProjectReminderRun(
                UUID.randomUUID(), workspaceId, runType, ReminderRunStatus.RUNNING,
                now, null, null, null, null, traceId, now, now);
    }

    public ProjectReminderRun complete(String summaryJson) {
        Instant now = Instant.now();
        return new ProjectReminderRun(
                id, workspaceId, runType, ReminderRunStatus.COMPLETED, startedAt, now,
                summaryJson, null, null, traceId, createdAt, now);
    }

    public ProjectReminderRun fail(String errorCode, String errorMessage) {
        Instant now = Instant.now();
        return new ProjectReminderRun(
                id, workspaceId, runType, ReminderRunStatus.FAILED, startedAt, now,
                resultSummaryJson, errorCode, errorMessage, traceId, createdAt, now);
    }
}
