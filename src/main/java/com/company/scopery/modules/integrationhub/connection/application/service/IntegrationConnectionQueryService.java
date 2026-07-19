package com.company.scopery.modules.integrationhub.connection.application.service;
import com.company.scopery.modules.integrationhub.connection.application.response.ConnectionHealthCheckResponse;
import com.company.scopery.modules.integrationhub.connection.application.response.IntegrationConnectionResponse;
import com.company.scopery.modules.integrationhub.connection.domain.model.ConnectionHealthCheckRepository;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class IntegrationConnectionQueryService {
    private final IntegrationConnectionRepository connections;
    private final ConnectionHealthCheckRepository healthChecks;
    private final IntegrationAuthorizationService auth;
    public IntegrationConnectionQueryService(IntegrationConnectionRepository connections,
            ConnectionHealthCheckRepository healthChecks, IntegrationAuthorizationService auth) {
        this.connections = connections; this.healthChecks = healthChecks; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<IntegrationConnectionResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return connections.findByWorkspaceId(workspaceId).stream().map(IntegrationConnectionResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public IntegrationConnectionResponse getById(UUID workspaceId, UUID connectionId) {
        auth.requireView(workspaceId);
        var c = connections.findById(connectionId).orElseThrow(() -> IntegrationExceptions.connectionNotFound(connectionId));
        if (!workspaceId.equals(c.workspaceId())) throw IntegrationExceptions.connectionNotFound(connectionId);
        return IntegrationConnectionResponse.from(c);
    }
    @Transactional(readOnly = true)
    public List<ConnectionHealthCheckResponse> listHealthChecks(UUID workspaceId, UUID connectionId) {
        auth.requireView(workspaceId);
        return healthChecks.findByConnectionId(connectionId).stream().map(ConnectionHealthCheckResponse::from).toList();
    }
}
