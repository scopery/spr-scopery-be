package com.company.scopery.modules.integrationhub.inbound.application.response;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEndpoint;
import java.time.Instant; import java.util.UUID;
public record InboundWebhookEndpointResponse(UUID id, UUID workspaceId, UUID connectionId, String endpointCode,
        String providerCode, String status, Instant createdAt, Instant updatedAt) {
    public static InboundWebhookEndpointResponse from(InboundWebhookEndpoint e) {
        return new InboundWebhookEndpointResponse(e.id(), e.workspaceId(), e.connectionId(), e.endpointCode(),
                e.providerCode(), e.status(), e.createdAt(), e.updatedAt());
    }
}
