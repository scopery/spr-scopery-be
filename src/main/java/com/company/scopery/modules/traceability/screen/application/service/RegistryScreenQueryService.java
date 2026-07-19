package com.company.scopery.modules.traceability.screen.application.service;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreenRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryScreenQueryService {
    private final RegistryScreenRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryScreenQueryService(RegistryScreenRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryScreenResponse> list(UUID workspaceId, UUID applicationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByApplicationId(applicationId).stream().map(RegistryScreenResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryScreenResponse get(UUID workspaceId, UUID applicationId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndApplicationId(id, applicationId).map(RegistryScreenResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.applicationNotFound(id));
    }
}
