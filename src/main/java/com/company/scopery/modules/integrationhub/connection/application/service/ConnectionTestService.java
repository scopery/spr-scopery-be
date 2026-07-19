package com.company.scopery.modules.integrationhub.connection.application.service;

import com.company.scopery.modules.integrationhub.connection.application.adapter.ConnectionTestAdapter;
import com.company.scopery.modules.integrationhub.connection.application.response.TestConnectionResult;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConnectionTestService {
    private final IntegrationConnectionRepository connections;
    private final IntegrationAuthorizationService auth;
    private final Map<String, ConnectionTestAdapter> adapters;

    public ConnectionTestService(
            IntegrationConnectionRepository connections,
            IntegrationAuthorizationService auth,
            List<ConnectionTestAdapter> adapterList) {
        this.connections = connections;
        this.auth = auth;
        this.adapters = adapterList.stream().collect(Collectors.toMap(ConnectionTestAdapter::providerCode, Function.identity()));
    }

    @Transactional(readOnly = true)
    public TestConnectionResult testConnection(UUID workspaceId, UUID connectionId) {
        auth.requireManage(workspaceId);
        var connection = connections.findById(connectionId)
                .orElseThrow(() -> IntegrationExceptions.connectionNotFound(connectionId));
        var adapter = adapters.get(connection.providerCode());
        if (adapter == null) {
            return TestConnectionResult.deferred(connection.providerCode());
        }
        return adapter.test(connection);
    }
}
