package com.company.scopery.modules.traceability.appcomponent.application.service;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryAppComponentQueryService {
    private final RegistryAppComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryAppComponentQueryService(RegistryAppComponentRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryAppComponentResponse> list(UUID applicationId) {
        return repo.findByApplicationId(applicationId).stream().map(RegistryAppComponentResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryAppComponentResponse get(UUID workspaceId, UUID appComponentId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(appComponentId, workspaceId).map(RegistryAppComponentResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(appComponentId));
    }
}
