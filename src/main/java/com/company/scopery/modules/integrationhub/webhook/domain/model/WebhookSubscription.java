package com.company.scopery.modules.integrationhub.webhook.domain.model;
import java.time.Instant; import java.util.UUID;
public record WebhookSubscription(UUID id, UUID workspaceId, UUID connectionId, String name, String endpointUrl,
        String eventTypesJson, String payloadVersion, UUID signingSecretReferenceId, String status,
        int maxAttempts, int timeoutSeconds, Instant disabledAt, Instant archivedAt,
        int version, Instant createdAt, Instant updatedAt) {

    public static WebhookSubscription create(UUID workspaceId, UUID connectionId, String name, String url,
            String eventTypesJson, String payloadVersion) {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("endpoint required");
        Instant now = Instant.now();
        return new WebhookSubscription(UUID.randomUUID(), workspaceId, connectionId, name, url, eventTypesJson,
                payloadVersion, null, "ACTIVE", 5, 10, null, null, 0, now, now);
    }

    public WebhookSubscription update(String name, String url, String eventTypesJson, String payloadVersion) {
        return new WebhookSubscription(id, workspaceId, connectionId, name, url, eventTypesJson, payloadVersion,
                signingSecretReferenceId, status, maxAttempts, timeoutSeconds, disabledAt, archivedAt, version, createdAt, Instant.now());
    }

    public WebhookSubscription enable() {
        return new WebhookSubscription(id, workspaceId, connectionId, name, endpointUrl, eventTypesJson, payloadVersion,
                signingSecretReferenceId, "ACTIVE", maxAttempts, timeoutSeconds, null, archivedAt, version, createdAt, Instant.now());
    }

    public WebhookSubscription disable() {
        return new WebhookSubscription(id, workspaceId, connectionId, name, endpointUrl, eventTypesJson, payloadVersion,
                signingSecretReferenceId, "DISABLED", maxAttempts, timeoutSeconds, Instant.now(), archivedAt, version, createdAt, Instant.now());
    }

    public WebhookSubscription archive() {
        return new WebhookSubscription(id, workspaceId, connectionId, name, endpointUrl, eventTypesJson, payloadVersion,
                signingSecretReferenceId, "ARCHIVED", maxAttempts, timeoutSeconds, disabledAt, Instant.now(), version, createdAt, Instant.now());
    }
}
