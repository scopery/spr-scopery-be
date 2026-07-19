package com.company.scopery.modules.traceability.apiendpoint.application.service;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryApiEndpointQueryService {
    private final RegistryApiEndpointRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryApiEndpointQueryService(RegistryApiEndpointRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryApiEndpointResponse> list(UUID workspaceId, UUID applicationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByApplicationId(applicationId).stream().map(RegistryApiEndpointResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryApiEndpointResponse get(UUID workspaceId, UUID applicationId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndApplicationId(id, applicationId).map(RegistryApiEndpointResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.applicationNotFound(id));
    }
}
