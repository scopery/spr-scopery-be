package com.company.scopery.modules.projectnotification.reminder.domain.model;

import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderEmissionStatus;
import com.company.scopery.modules.projectnotification.reminder.domain.enums.ReminderType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectReminderEmission(
        UUID id,
        UUID reminderRunId,
        UUID projectId,
        UUID taskId,
        UUID milestoneId,
        UUID recipientUserId,
        ReminderType reminderType,
        LocalDate reminderDate,
        String dedupKey,
        ReminderEmissionStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectReminderEmission emitted(
            UUID reminderRunId, UUID projectId, UUID taskId, UUID recipientUserId,
            ReminderType type, LocalDate reminderDate, String dedupKey) {
        Instant now = Instant.now();
        return new ProjectReminderEmission(
                UUID.randomUUID(), reminderRunId, projectId, taskId, null, recipientUserId,
                type, reminderDate, dedupKey, ReminderEmissionStatus.EMITTED, now, now);
    }

    public static String buildDedupKey(UUID taskId, ReminderType type, LocalDate date, UUID recipientUserId) {
        return taskId + ":" + type.name() + ":" + date + ":" + recipientUserId;
    }
}
