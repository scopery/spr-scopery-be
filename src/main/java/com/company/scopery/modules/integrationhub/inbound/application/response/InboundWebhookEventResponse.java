package com.company.scopery.modules.integrationhub.inbound.application.response;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEvent;
import java.time.Instant; import java.util.UUID;
public record InboundWebhookEventResponse(UUID id, UUID inboundEndpointId, String providerCode, String eventType,
        String status, String externalEventId, Instant receivedAt, Instant processedAt,
        String failureCode, String failureMessage) {
    public static InboundWebhookEventResponse from(InboundWebhookEvent e) {
        return new InboundWebhookEventResponse(e.id(), e.inboundEndpointId(), e.providerCode(), e.eventType(),
                e.status(), e.externalEventId(), e.receivedAt(), e.processedAt(),
                e.failureCode(), e.failureMessage());
    }
}
