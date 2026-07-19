package com.company.scopery.modules.notification.advanced.digest.domain.model;
import java.time.Instant; import java.util.UUID;
public record DigestRun(UUID id, UUID workspaceId, UUID digestRuleId, UUID recipientUserId,
        String status, int notificationCount, Instant sentAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static DigestRun create(UUID workspaceId, UUID digestRuleId, UUID recipientUserId, int notificationCount) {
        Instant now = Instant.now();
        return new DigestRun(UUID.randomUUID(), workspaceId, digestRuleId, recipientUserId,
                "CREATED", notificationCount, null, 0, now, now);
    }
    public DigestRun markSent() {
        return new DigestRun(id, workspaceId, digestRuleId, recipientUserId,
                "SENT", notificationCount, Instant.now(), version, createdAt, Instant.now());
    }
    public DigestRun markFailed() {
        return new DigestRun(id, workspaceId, digestRuleId, recipientUserId,
                "FAILED", notificationCount, sentAt, version, createdAt, Instant.now());
    }
    public DigestRun markSkipped() {
        return new DigestRun(id, workspaceId, digestRuleId, recipientUserId,
                "SKIPPED", notificationCount, sentAt, version, createdAt, Instant.now());
    }
}
