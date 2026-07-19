package com.company.scopery.modules.notification.advanced.alert.domain.model;
import java.time.Instant; import java.util.UUID;
public record AlertRule(UUID id, UUID workspaceId, String ruleCode, String name, String category, String conditionJson,
        boolean bypassQuietHours, String status, int version, Instant createdAt, Instant updatedAt) {
    public static AlertRule create(UUID workspaceId, String code, String name, String category, String condition, boolean bypass) {
        Instant now = Instant.now();
        return new AlertRule(UUID.randomUUID(), workspaceId, code, name, category, condition, bypass, "ACTIVE", 0, now, now);
    }
}
