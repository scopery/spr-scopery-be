package com.company.scopery.modules.integrationhub.inbound.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface InboundWebhookEventRepository {
    InboundWebhookEvent save(InboundWebhookEvent e);
    Optional<InboundWebhookEvent> findById(UUID id);
    Optional<InboundWebhookEvent> findByInboundEndpointIdAndExternalEventId(UUID endpointId, String externalEventId);
    List<InboundWebhookEvent> findByWorkspaceId(UUID workspaceId);
}
