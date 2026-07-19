package com.company.scopery.modules.traceability.tracelink.domain.model;

import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkStatus;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkType;
import java.time.Instant;
import java.util.UUID;

public record TraceLink(UUID id, UUID projectId, String sourceType, UUID sourceId,
                        String targetType, UUID targetId, TraceLinkType linkType,
                        TraceLinkStatus status, Instant archivedAt, int version, Instant createdAt) {
    public static TraceLink create(UUID projectId, String sourceType, UUID sourceId,
                                   String targetType, UUID targetId, TraceLinkType linkType) {
        return new TraceLink(UUID.randomUUID(), projectId, sourceType, sourceId, targetType, targetId,
                linkType, TraceLinkStatus.ACTIVE, null, 0, Instant.now());
    }

    public TraceLink archive() {
        return new TraceLink(id, projectId, sourceType, sourceId, targetType, targetId,
                linkType, TraceLinkStatus.ARCHIVED, Instant.now(), version, createdAt);
    }
}
