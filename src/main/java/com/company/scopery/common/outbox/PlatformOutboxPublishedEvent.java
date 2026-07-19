package com.company.scopery.common.outbox;

import java.util.UUID;

/**
 * Published after a transactional outbox row is successfully processed.
 * Email/notification handoff remains on the existing AFTER_COMMIT path in Phase 04;
 * future phases may consume this event for generic fan-out.
 */
public record PlatformOutboxPublishedEvent(
        UUID outboxId,
        String eventType,
        String sourceSystem,
        int eventVersion,
        String aggregateType,
        UUID aggregateId,
        String payloadJson,
        String traceId
) {
}
