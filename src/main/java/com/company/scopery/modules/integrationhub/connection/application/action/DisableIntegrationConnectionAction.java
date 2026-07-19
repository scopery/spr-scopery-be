package com.company.scopery.modules.integrationhub.connection.application.action;
import com.company.scopery.modules.integrationhub.connection.application.response.IntegrationConnectionResponse;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DisableIntegrationConnectionAction {
    private final IntegrationConnectionRepository connections;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public DisableIntegrationConnectionAction(IntegrationConnectionRepository connections,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.connections = connections; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public IntegrationConnectionResponse execute(UUID workspaceId, UUID connectionId) {
        auth.requireManage(workspaceId);
        var c = connections.findById(connectionId).orElseThrow(() -> IntegrationExceptions.connectionNotFound(connectionId));
        if (!workspaceId.equals(c.workspaceId())) throw IntegrationExceptions.connectionNotFound(connectionId);
        var saved = connections.save(c.disable());
        activity.logSuccess("INTEGRATION_CONNECTION", saved.id(), "INTEGRATION_CONNECTION_DISABLED", "Connection disabled");
        return IntegrationConnectionResponse.from(saved);
    }
}
