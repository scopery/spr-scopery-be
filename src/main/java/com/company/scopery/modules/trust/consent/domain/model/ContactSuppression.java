package com.company.scopery.modules.trust.consent.domain.model;
import java.time.Instant; import java.util.UUID;
public record ContactSuppression(UUID id, UUID workspaceId, UUID externalContactId,
        UUID portalAccountId, String suppressionType, String reason, String status,
        Instant releasedAt, String releaseReason, int version, Instant createdAt, Instant updatedAt) {
    public static ContactSuppression create(UUID workspaceId, UUID externalContactId, UUID portalAccountId,
            String suppressionType, String reason) {
        Instant now = Instant.now();
        return new ContactSuppression(UUID.randomUUID(), workspaceId, externalContactId, portalAccountId,
                suppressionType, reason, "ACTIVE", null, null, 0, now, now);
    }
    public ContactSuppression release(String releaseReason) {
        return new ContactSuppression(id, workspaceId, externalContactId, portalAccountId, suppressionType,
                reason, "RELEASED", Instant.now(), releaseReason, version, createdAt, Instant.now());
    }
}
