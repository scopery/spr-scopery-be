package com.company.scopery.modules.trust.legalhold.domain.model;
import java.time.Instant; import java.util.UUID;
public record LegalHold(UUID id, UUID workspaceId, String holdCode, String holdType, String scopeType, UUID scopeId,
        String reason, String status, Instant releasedAt, String releaseReason, int version, Instant createdAt, Instant updatedAt) {
    public static LegalHold create(UUID workspaceId, String holdType, String scopeType, UUID scopeId, String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("reason required");
        Instant now = Instant.now();
        return new LegalHold(UUID.randomUUID(), workspaceId, "LH-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(),
                holdType, scopeType, scopeId, reason, "ACTIVE", null, null, 0, now, now);
    }
    public LegalHold release(String releaseReason) {
        if (releaseReason == null || releaseReason.isBlank()) throw new IllegalArgumentException("release reason required");
        if (!"ACTIVE".equals(status)) throw new IllegalStateException("Only ACTIVE can be released");
        return new LegalHold(id, workspaceId, holdCode, holdType, scopeType, scopeId, reason, "RELEASED",
                Instant.now(), releaseReason, version, createdAt, Instant.now());
    }
    public boolean isActive() { return "ACTIVE".equals(status); }
}
