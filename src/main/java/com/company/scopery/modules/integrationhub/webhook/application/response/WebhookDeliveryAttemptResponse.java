package com.company.scopery.modules.integrationhub.webhook.application.response;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookDeliveryAttempt;
import java.time.Instant; import java.util.UUID;
public record WebhookDeliveryAttemptResponse(UUID id, UUID webhookSubscriptionId, String eventType, String status,
        int attemptNumber, String responseBodyRedacted, Instant sentAt, Instant createdAt) {
    public static WebhookDeliveryAttemptResponse from(WebhookDeliveryAttempt a) {
        return new WebhookDeliveryAttemptResponse(a.id(), a.webhookSubscriptionId(), a.eventType(), a.status(),
                a.attemptNumber(), a.responseBodyRedacted(), a.sentAt(), a.createdAt());
    }
}
