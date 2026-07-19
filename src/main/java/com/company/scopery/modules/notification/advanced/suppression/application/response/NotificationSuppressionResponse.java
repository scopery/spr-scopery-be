package com.company.scopery.modules.notification.advanced.suppression.application.response;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionEntry;
import java.time.Instant; import java.util.UUID;
public record NotificationSuppressionResponse(UUID id, UUID workspaceId, UUID projectId, UUID userId,
        String categoryCode, String channelCode, String reasonCode, String sourceType, UUID sourceId,
        Instant suppressedAt, Instant expiresAt, Instant createdAt) {
    public static NotificationSuppressionResponse from(NotificationSuppressionEntry e) {
        return new NotificationSuppressionResponse(e.id(), e.workspaceId(), e.projectId(), e.userId(),
                e.categoryCode(), e.channelCode(), e.reasonCode(), e.sourceType(), e.sourceId(),
                e.suppressedAt(), e.expiresAt(), e.createdAt());
    }
}
