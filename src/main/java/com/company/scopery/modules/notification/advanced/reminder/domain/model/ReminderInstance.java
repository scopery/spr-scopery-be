package com.company.scopery.modules.notification.advanced.reminder.domain.model;
import java.time.Instant; import java.util.UUID;
public record ReminderInstance(UUID id, UUID workspaceId, UUID reminderRuleId, String sourceType, UUID sourceId,
        UUID recipientUserId, Instant remindAt, String status, String dedupKey,
        Instant snoozedUntil, Instant dismissedAt, int version, Instant createdAt, Instant updatedAt) {
    public static ReminderInstance create(UUID workspaceId, UUID reminderRuleId, String sourceType, UUID sourceId,
                                          UUID recipientUserId, Instant remindAt, String dedupKey) {
        Instant now = Instant.now();
        return new ReminderInstance(UUID.randomUUID(), workspaceId, reminderRuleId, sourceType, sourceId,
                recipientUserId, remindAt, "PENDING", dedupKey, null, null, 0, now, now);
    }
    public ReminderInstance dispatch() {
        return new ReminderInstance(id, workspaceId, reminderRuleId, sourceType, sourceId,
                recipientUserId, remindAt, "DISPATCHED", dedupKey, snoozedUntil, dismissedAt, version, createdAt, Instant.now());
    }
    public ReminderInstance snooze(Instant until) {
        return new ReminderInstance(id, workspaceId, reminderRuleId, sourceType, sourceId,
                recipientUserId, remindAt, "SNOOZED", dedupKey, until, dismissedAt, version, createdAt, Instant.now());
    }
    public ReminderInstance dismiss() {
        return new ReminderInstance(id, workspaceId, reminderRuleId, sourceType, sourceId,
                recipientUserId, remindAt, "DISMISSED", dedupKey, snoozedUntil, Instant.now(), version, createdAt, Instant.now());
    }
}
