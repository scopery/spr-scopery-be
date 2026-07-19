package com.company.scopery.modules.trust.consent.application.response;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppression;
import java.time.Instant; import java.util.UUID;
public record ContactSuppressionResponse(UUID id, UUID workspaceId, UUID externalContactId,
        UUID portalAccountId, String suppressionType, String reason, String status,
        Instant releasedAt, String releaseReason, Instant createdAt) {
    public static ContactSuppressionResponse from(ContactSuppression s) {
        return new ContactSuppressionResponse(s.id(), s.workspaceId(), s.externalContactId(),
                s.portalAccountId(), s.suppressionType(), s.reason(), s.status(),
                s.releasedAt(), s.releaseReason(), s.createdAt());
    }
}
