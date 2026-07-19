package com.company.scopery.modules.notification.advanced.reminder.application.command;
import java.time.Instant; import java.util.UUID;
public record SnoozeReminderInstanceCommand(UUID workspaceId, UUID reminderInstanceId, Instant snoozedUntil) {}
