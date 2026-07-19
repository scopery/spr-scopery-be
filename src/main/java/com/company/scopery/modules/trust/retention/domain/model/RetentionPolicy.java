package com.company.scopery.modules.trust.retention.domain.model;
import java.time.Instant; import java.util.UUID;
public record RetentionPolicy(UUID id, UUID workspaceId, String policyCode, String name, String objectTypeCode,
        int retentionPeriodDays, String retentionAction, boolean reviewRequired, boolean enabled, int version, Instant createdAt) {
    public static RetentionPolicy create(UUID workspaceId, String code, String name, String objectType, int days, String action) {
        return new RetentionPolicy(UUID.randomUUID(), workspaceId, code, name, objectType, days, action, true, true, 0, Instant.now());
    }
}
