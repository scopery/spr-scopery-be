package com.company.scopery.modules.notification.advanced.alert.domain.model;
import java.time.Instant; import java.util.UUID;
public record AlertEvent(UUID id, UUID workspaceId, UUID alertRuleId, String sourceType, UUID sourceId,
        String severity, String title, String status, String dedupKey,
        Instant acknowledgedAt, Instant dismissedAt, int version, Instant createdAt, Instant updatedAt) {
    public static AlertEvent create(UUID workspaceId, UUID alertRuleId, String sourceType, UUID sourceId,
                                    String severity, String title, String dedupKey) {
        Instant now = Instant.now();
        return new AlertEvent(UUID.randomUUID(), workspaceId, alertRuleId, sourceType, sourceId,
                severity, title, "OPEN", dedupKey, null, null, 0, now, now);
    }
    public AlertEvent acknowledge() {
        return new AlertEvent(id, workspaceId, alertRuleId, sourceType, sourceId, severity, title,
                "ACKNOWLEDGED", dedupKey, Instant.now(), dismissedAt, version, createdAt, Instant.now());
    }
    public AlertEvent dismiss() {
        return new AlertEvent(id, workspaceId, alertRuleId, sourceType, sourceId, severity, title,
                "DISMISSED", dedupKey, acknowledgedAt, Instant.now(), version, createdAt, Instant.now());
    }
}
