package com.company.scopery.modules.reporting.activityfeed.application.response;

import com.company.scopery.common.audit.ImmutableAuditEventJpaEntity;

import java.time.Instant;
import java.util.UUID;

/** Tenant-facing activity feed row backed by immutable audit events. */
public record ScopedActivityFeedItemResponse(
        UUID id,
        Instant occurredAt,
        String eventType,
        String severity,
        UUID actorId,
        String actorType,
        String resourceType,
        UUID resourceRefId,
        String reason
) {
    public static ScopedActivityFeedItemResponse from(ImmutableAuditEventJpaEntity entity) {
        return new ScopedActivityFeedItemResponse(
                entity.getId(),
                entity.getOccurredAt(),
                entity.getEventType(),
                entity.getSeverity(),
                entity.getActorId(),
                entity.getActorType(),
                entity.getResourceType(),
                entity.getResourceRefId(),
                entity.getReason());
    }
}
