package com.company.scopery.modules.notification.advanced.reminder.application.response;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.ReminderInstance;
import java.time.Instant; import java.util.UUID;
public record ReminderInstanceResponse(UUID id, UUID workspaceId, UUID reminderRuleId, String sourceType, UUID sourceId,
        UUID recipientUserId, Instant remindAt, String status, String dedupKey, Instant snoozedUntil, Instant dismissedAt, Instant createdAt) {
    public static ReminderInstanceResponse from(ReminderInstance r) {
        return new ReminderInstanceResponse(r.id(), r.workspaceId(), r.reminderRuleId(), r.sourceType(), r.sourceId(),
                r.recipientUserId(), r.remindAt(), r.status(), r.dedupKey(), r.snoozedUntil(), r.dismissedAt(), r.createdAt());
    }
}
