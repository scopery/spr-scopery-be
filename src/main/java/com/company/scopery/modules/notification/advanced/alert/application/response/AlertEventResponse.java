package com.company.scopery.modules.notification.advanced.alert.application.response;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertEvent;
import java.time.Instant; import java.util.UUID;
public record AlertEventResponse(UUID id, UUID workspaceId, UUID alertRuleId, String sourceType, UUID sourceId,
        String severity, String title, String status, String dedupKey,
        Instant acknowledgedAt, Instant dismissedAt, Instant createdAt) {
    public static AlertEventResponse from(AlertEvent e) {
        return new AlertEventResponse(e.id(), e.workspaceId(), e.alertRuleId(), e.sourceType(), e.sourceId(),
                e.severity(), e.title(), e.status(), e.dedupKey(), e.acknowledgedAt(), e.dismissedAt(), e.createdAt());
    }
}
