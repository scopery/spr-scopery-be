package com.company.scopery.modules.integrationhub.inbound.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface InboundWebhookEndpointRepository {
    InboundWebhookEndpoint save(InboundWebhookEndpoint e);
    Optional<InboundWebhookEndpoint> findById(UUID id);
    Optional<InboundWebhookEndpoint> findByEndpointCode(String code);
    List<InboundWebhookEndpoint> findByWorkspaceId(UUID workspaceId);
}
