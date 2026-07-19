package com.company.scopery.modules.integrationhub.inbound.application.action;
import com.company.scopery.modules.integrationhub.inbound.application.response.InboundWebhookEndpointResponse;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEndpoint;
import com.company.scopery.modules.integrationhub.inbound.domain.model.InboundWebhookEndpointRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateInboundWebhookEndpointAction {
    private final InboundWebhookEndpointRepository endpoints;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateInboundWebhookEndpointAction(InboundWebhookEndpointRepository endpoints, IntegrationAuthorizationService auth,
            IntegrationActivityLogger activity) {
        this.endpoints = endpoints; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public InboundWebhookEndpointResponse execute(UUID workspaceId, UUID connectionId, String endpointCode, String providerCode) {
        auth.requireManage(workspaceId);
        var saved = endpoints.save(InboundWebhookEndpoint.create(workspaceId, connectionId, endpointCode, providerCode));
        activity.logSuccess("INTEGRATION_INBOUND_ENDPOINT", saved.id(), "INTEGRATION_INBOUND_ENDPOINT_CREATED",
                "Inbound endpoint created: " + endpointCode);
        return InboundWebhookEndpointResponse.from(saved);
    }
}
