package com.company.scopery.modules.integrationhub.deadletter.application.response;
import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEvent;
import java.time.Instant; import java.util.UUID;
public record DeadLetterEventResponse(UUID id, String sourceType, UUID sourceId, String eventType,
        String failureCode, String failureMessage, int attemptCount, String status,
        Instant lastAttemptAt, Instant resolvedAt, Instant createdAt) {
    public static DeadLetterEventResponse from(DeadLetterEvent e) {
        return new DeadLetterEventResponse(e.id(), e.sourceType(), e.sourceId(), e.eventType(),
                e.failureCode(), e.failureMessage(), e.attemptCount(), e.status(),
                e.lastAttemptAt(), e.resolvedAt(), e.createdAt());
    }
}
