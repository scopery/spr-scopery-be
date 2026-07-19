package com.company.scopery.modules.notification.advanced.digest.domain.model;
import java.time.Instant; import java.util.UUID;
public record DigestRule(UUID id, UUID workspaceId, String code, String name, String scope, String frequency,
        String scheduleConfigJson, String status, int version, Instant createdAt, Instant updatedAt) {
    public static DigestRule create(UUID workspaceId, String code, String name, String scope, String frequency, String schedule) {
        Instant now = Instant.now();
        return new DigestRule(UUID.randomUUID(), workspaceId, code, name, scope, frequency, schedule, "ACTIVE", 0, now, now);
    }
    public DigestRule update(String name, String scope, String frequency, String schedule, String status) {
        return new DigestRule(id, workspaceId, code,
                name != null ? name : this.name,
                scope != null ? scope : this.scope,
                frequency != null ? frequency : this.frequency,
                schedule != null ? schedule : this.scheduleConfigJson,
                status != null ? status : this.status,
                version, createdAt, Instant.now());
    }
}
