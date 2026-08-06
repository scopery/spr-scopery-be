package com.company.scopery.modules.specpack.outline.application.response;

import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutline;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record OutlineResponse(
        UUID id,
        UUID sessionId,
        int versionNumber,
        Map<String, Object> outlineJson,
        String status,
        Instant approvedAt,
        String createdBy,
        Instant createdAt
) {
    public static OutlineResponse from(SpecPackOutline outline) {
        return new OutlineResponse(
                outline.id(),
                outline.sessionId(),
                outline.versionNumber(),
                outline.outlineJson(),
                outline.status().name(),
                outline.approvedAt(),
                outline.createdBy(),
                outline.createdAt()
        );
    }
}
