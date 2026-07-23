package com.company.scopery.modules.traceability.nfrscope.application.response;

import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTarget;

import java.time.Instant;
import java.util.UUID;

public record NfrScopeTargetResponse(UUID nfrId, UUID targetId, String targetType, Instant createdAt) {
    public static NfrScopeTargetResponse from(NfrScopeTarget t) {
        return new NfrScopeTargetResponse(
                t.nfrId(), t.targetId(),
                t.targetType() != null ? t.targetType().name() : null,
                t.createdAt()
        );
    }
}
