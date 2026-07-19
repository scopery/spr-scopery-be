package com.company.scopery.modules.integrationhub.inbound.application.service;
import com.company.scopery.modules.integrationhub.inbound.application.response.InboundWebhookEndpointResponse;
import com.company.scopery.modules.integrationhub.inbound.application.response.InboundWebhookEventResponse;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEndpointRepository;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEventRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class InboundWebhookQueryService {
    private final InboundWebhookEndpointRepository endpoints;
    private final InboundWebhookEventRepository events;
    private final IntegrationAuthorizationService auth;
    public InboundWebhookQueryService(InboundWebhookEndpointRepository endpoints, InboundWebhookEventRepository events,
            IntegrationAuthorizationService auth) {
        this.endpoints = endpoints; this.events = events; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<InboundWebhookEndpointResponse> listEndpoints(UUID workspaceId) {
        auth.requireView(workspaceId);
        return endpoints.findByWorkspaceId(workspaceId).stream().map(InboundWebhookEndpointResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public List<InboundWebhookEventResponse> listEvents(UUID workspaceId) {
        auth.requireView(workspaceId);
        return events.findByWorkspaceId(workspaceId).stream().map(InboundWebhookEventResponse::from).toList();
    }
}
