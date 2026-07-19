package com.company.scopery.modules.productivity.workinbox.domain.model;
import java.time.Instant; import java.util.UUID;
public record WorkInboxItem(UUID id, UUID workspaceId, UUID userId, String sourceType, UUID sourceId, String actionType, String title,
        String priority, Instant dueAt, String status, Instant readAt, Instant dismissedAt, Instant snoozedUntil,
        int version, Instant createdAt, Instant updatedAt) {
    public static WorkInboxItem create(UUID workspaceId, UUID userId, String sourceType, UUID sourceId, String actionType, String title, String priority, Instant dueAt) {
        Instant now = Instant.now();
        return new WorkInboxItem(UUID.randomUUID(), workspaceId, userId, sourceType, sourceId, actionType, title, priority, dueAt, "ACTIVE", null, null, null, 0, now, now);
    }
    public WorkInboxItem markRead() { return new WorkInboxItem(id, workspaceId, userId, sourceType, sourceId, actionType, title, priority, dueAt, "READ", Instant.now(), dismissedAt, snoozedUntil, version, createdAt, Instant.now()); }
    public WorkInboxItem dismiss() { return new WorkInboxItem(id, workspaceId, userId, sourceType, sourceId, actionType, title, priority, dueAt, "DISMISSED", readAt, Instant.now(), snoozedUntil, version, createdAt, Instant.now()); }
    public WorkInboxItem snooze(Instant until) { return new WorkInboxItem(id, workspaceId, userId, sourceType, sourceId, actionType, title, priority, dueAt, "SNOOZED", readAt, dismissedAt, until, version, createdAt, Instant.now()); }
}
