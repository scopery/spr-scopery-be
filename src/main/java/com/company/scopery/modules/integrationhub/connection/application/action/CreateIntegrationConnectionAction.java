package com.company.scopery.modules.integrationhub.connection.application.action;
import com.company.scopery.modules.integrationhub.connection.application.response.IntegrationConnectionResponse;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateIntegrationConnectionAction {
    private final IntegrationConnectionRepository connections;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateIntegrationConnectionAction(IntegrationConnectionRepository connections,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.connections = connections; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public IntegrationConnectionResponse execute(UUID workspaceId, String providerCode, String name, UUID credentialReferenceId) {
        auth.requireManage(workspaceId);
        var saved = connections.save(IntegrationConnection.create(workspaceId, providerCode, name, credentialReferenceId).activate());
        activity.logSuccess("INTEGRATION_CONNECTION", saved.id(), "INTEGRATION_CONNECTION_CREATED", "Connection created");
        return IntegrationConnectionResponse.from(saved);
    }
}
