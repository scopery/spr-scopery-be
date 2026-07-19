package com.company.scopery.modules.notification.advanced.digest.application.response;
import com.company.scopery.modules.notification.advanced.digest.domain.model.DigestRun;
import java.time.Instant; import java.util.UUID;
public record DigestRunResponse(UUID id, UUID workspaceId, UUID digestRuleId, UUID recipientUserId,
        String status, int notificationCount, Instant sentAt, Instant createdAt) {
    public static DigestRunResponse from(DigestRun r) {
        return new DigestRunResponse(r.id(), r.workspaceId(), r.digestRuleId(), r.recipientUserId(),
                r.status(), r.notificationCount(), r.sentAt(), r.createdAt());
    }
}
