package com.company.scopery.modules.integrationhub.inbound.domain.model;
import java.time.Instant; import java.util.UUID;
public record InboundWebhookEndpoint(UUID id, UUID workspaceId, UUID connectionId, String endpointCode,
        String providerCode, UUID signingSecretReferenceId, String status, Instant disabledAt,
        int version, Instant createdAt, Instant updatedAt) {

    public static InboundWebhookEndpoint create(UUID workspaceId, UUID connectionId, String code, String providerCode) {
        Instant now = Instant.now();
        return new InboundWebhookEndpoint(UUID.randomUUID(), workspaceId, connectionId, code, providerCode, null,
                "ACTIVE", null, 0, now, now);
    }

    public InboundWebhookEndpoint disable() {
        return new InboundWebhookEndpoint(id, workspaceId, connectionId, endpointCode, providerCode,
                signingSecretReferenceId, "DISABLED", Instant.now(), version, createdAt, Instant.now());
    }
}
