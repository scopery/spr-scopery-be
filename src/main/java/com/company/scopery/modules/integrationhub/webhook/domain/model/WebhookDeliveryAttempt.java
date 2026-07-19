package com.company.scopery.modules.integrationhub.webhook.domain.model;

import java.time.Instant;
import java.util.UUID;

public record WebhookDeliveryAttempt(
        UUID id,
        UUID workspaceId,
        UUID webhookSubscriptionId,
        String eventType,
        String status,
        int attemptNumber,
        String responseBodyRedacted,
        Instant sentAt,
        int version,
        Instant createdAt) {

    public static WebhookDeliveryAttempt recordAttempt(
            UUID workspaceId, UUID subscriptionId, String eventType, int attemptNumber,
            boolean success, String responseBody) {
        String status = success ? "SENT" : (attemptNumber >= 5 ? "DEAD_LETTERED" : "RETRYING");
        return new WebhookDeliveryAttempt(
                UUID.randomUUID(), workspaceId, subscriptionId, eventType, status, attemptNumber,
                responseBody, Instant.now(), 0, Instant.now());
    }
}
