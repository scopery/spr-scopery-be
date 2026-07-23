package com.company.scopery.modules.traceability.nfrscope.domain.model;

import com.company.scopery.modules.traceability.nfrscope.domain.enums.NfrTargetType;

import java.time.Instant;
import java.util.UUID;

public record NfrScopeTarget(UUID nfrId, UUID targetId, NfrTargetType targetType, Instant createdAt) {
    public static NfrScopeTarget create(UUID nfrId, UUID targetId, NfrTargetType targetType) {
        return new NfrScopeTarget(nfrId, targetId, targetType, Instant.now());
    }
}
