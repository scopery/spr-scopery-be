package com.company.scopery.modules.integrationhub.connection.application.action;
import com.company.scopery.modules.integrationhub.connection.application.response.ConnectionHealthCheckResponse;
import com.company.scopery.modules.integrationhub.connection.application.service.ConnectionTestService;
import com.company.scopery.modules.integrationhub.connection.domain.model.ConnectionHealthCheck;
import com.company.scopery.modules.integrationhub.connection.domain.model.ConnectionHealthCheckRepository;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RunConnectionHealthCheckAction {
    private final IntegrationConnectionRepository connections;
    private final ConnectionHealthCheckRepository healthChecks;
    private final ConnectionTestService connectionTest;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public RunConnectionHealthCheckAction(IntegrationConnectionRepository connections,
            ConnectionHealthCheckRepository healthChecks, ConnectionTestService connectionTest,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.connections = connections; this.healthChecks = healthChecks; this.connectionTest = connectionTest;
        this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ConnectionHealthCheckResponse execute(UUID workspaceId, UUID connectionId) {
        auth.requireManage(workspaceId);
        var c = connections.findById(connectionId).orElseThrow(() -> IntegrationExceptions.connectionNotFound(connectionId));
        if (!workspaceId.equals(c.workspaceId())) throw IntegrationExceptions.connectionNotFound(connectionId);
        var test = connectionTest.testConnection(workspaceId, connectionId);
        String health = test.configValid() ? "HEALTHY" : "UNHEALTHY";
        connections.save(c.withHealth(health));
        var check = healthChecks.save(ConnectionHealthCheck.record(workspaceId, connectionId, health, null, test.message()));
        activity.logSuccess("INTEGRATION_CONNECTION", connectionId, "INTEGRATION_CONNECTION_HEALTH_CHECKED",
                "Health check: " + health);
        return ConnectionHealthCheckResponse.from(check);
    }
}
