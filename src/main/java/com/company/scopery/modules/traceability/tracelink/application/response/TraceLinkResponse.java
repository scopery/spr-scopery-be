package com.company.scopery.modules.traceability.tracelink.application.response;

import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import java.time.Instant;
import java.util.UUID;

public record TraceLinkResponse(UUID id, UUID projectId, String sourceType, UUID sourceId,
                                 String targetType, UUID targetId, String linkType,
                                 String status, Instant createdAt) {
    public static TraceLinkResponse from(TraceLink e) {
        return new TraceLinkResponse(e.id(), e.projectId(), e.sourceType(), e.sourceId(),
                e.targetType(), e.targetId(), e.linkType().name(), e.status().name(), e.createdAt());
    }
}
