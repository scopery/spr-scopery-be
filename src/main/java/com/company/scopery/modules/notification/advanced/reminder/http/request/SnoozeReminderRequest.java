package com.company.scopery.modules.notification.advanced.reminder.http.request;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
public record SnoozeReminderRequest(@NotNull Instant snoozedUntil) {}
