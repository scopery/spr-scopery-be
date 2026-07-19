package com.company.scopery.modules.integrationhub.webhook.application.response;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscription;
import java.time.Instant; import java.util.UUID;
public record WebhookSubscriptionResponse(UUID id, UUID workspaceId, UUID connectionId, String name, String endpointUrl,
        String eventTypesJson, String payloadVersion, String status, int maxAttempts, int timeoutSeconds,
        Instant createdAt, Instant updatedAt) {
    public static WebhookSubscriptionResponse from(WebhookSubscription s) {
        return new WebhookSubscriptionResponse(s.id(), s.workspaceId(), s.connectionId(), s.name(), s.endpointUrl(),
                s.eventTypesJson(), s.payloadVersion(), s.status(), s.maxAttempts(), s.timeoutSeconds(),
                s.createdAt(), s.updatedAt());
    }
}
