package com.company.scopery.modules.trust.legalhold.application.response;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHold;
import java.time.Instant; import java.util.UUID;
public record LegalHoldResponse(UUID id, UUID workspaceId, String holdCode, String holdType, String scopeType,
        UUID scopeId, String reason, String status, Instant releasedAt, String releaseReason) {
    public static LegalHoldResponse from(LegalHold h) {
        return new LegalHoldResponse(h.id(), h.workspaceId(), h.holdCode(), h.holdType(), h.scopeType(),
                h.scopeId(), h.reason(), h.status(), h.releasedAt(), h.releaseReason());
    }
}
