package com.company.scopery.modules.integrationhub.inbound.domain.model;
import java.time.Instant; import java.util.UUID;
public record InboundWebhookEvent(UUID id, UUID workspaceId, UUID inboundEndpointId, String providerCode,
        String externalEventId, String eventType, String status, String payloadReference, String payloadRedactedJson,
        Instant receivedAt, Instant processedAt, String failureCode, String failureMessage,
        int version, Instant createdAt) {

    public static InboundWebhookEvent receive(UUID workspaceId, UUID endpointId, String providerCode,
            String eventType, String externalEventId, String payloadRedactedJson) {
        Instant now = Instant.now();
        return new InboundWebhookEvent(UUID.randomUUID(), workspaceId, endpointId, providerCode,
                externalEventId, eventType, "RECEIVED", null, payloadRedactedJson,
                now, null, null, null, 0, now);
    }

    public InboundWebhookEvent markProcessed() {
        return new InboundWebhookEvent(id, workspaceId, inboundEndpointId, providerCode, externalEventId,
                eventType, "PROCESSED", payloadReference, payloadRedactedJson,
                receivedAt, Instant.now(), null, null, version, createdAt);
    }

    public InboundWebhookEvent markFailed(String failureCode, String message) {
        return new InboundWebhookEvent(id, workspaceId, inboundEndpointId, providerCode, externalEventId,
                eventType, "FAILED", payloadReference, payloadRedactedJson,
                receivedAt, Instant.now(), failureCode, message, version, createdAt);
    }
}
