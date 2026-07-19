package com.company.scopery.modules.integrationhub.deadletter.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DeadLetterEvent(
        UUID id,
        UUID workspaceId,
        String sourceType,
        UUID sourceId,
        String eventType,
        String payloadReference,
        String failureCode,
        String failureMessage,
        int attemptCount,
        String status,
        Instant lastAttemptAt,
        Instant resolvedAt,
        UUID resolvedBy,
        int version,
        Instant createdAt) {

    public static DeadLetterEvent fromWebhookFailure(
            UUID workspaceId, UUID deliveryAttemptId, String eventType,
            String payloadReference, int attemptCount, String failureMessage) {
        Instant now = Instant.now();
        return new DeadLetterEvent(
                UUID.randomUUID(),
                workspaceId,
                "WEBHOOK_DELIVERY",
                deliveryAttemptId,
                eventType,
                payloadReference,
                "WEBHOOK_DELIVERY_EXHAUSTED",
                failureMessage,
                attemptCount,
                "OPEN",
                now,
                null,
                null,
                0,
                now);
    }

    public DeadLetterEvent resolve(UUID actorId) {
        return new DeadLetterEvent(
                id, workspaceId, sourceType, sourceId, eventType, payloadReference,
                failureCode, failureMessage, attemptCount, "RESOLVED",
                lastAttemptAt, Instant.now(), actorId, version, createdAt);
    }

    public DeadLetterEvent retry() {
        return new DeadLetterEvent(
                id, workspaceId, sourceType, sourceId, eventType, payloadReference,
                failureCode, failureMessage, attemptCount + 1, "RETRYING",
                Instant.now(), null, null, version, createdAt);
    }
}
