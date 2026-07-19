package com.company.scopery.modules.traceability.dataentity.application.service;
import com.company.scopery.modules.traceability.dataentity.application.response.RegistryDataEntityResponse;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryDataEntityQueryService {
    private final RegistryDataEntityRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryDataEntityQueryService(RegistryDataEntityRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<RegistryDataEntityResponse> list(UUID workspaceId, UUID applicationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByApplicationId(applicationId).stream().map(RegistryDataEntityResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public RegistryDataEntityResponse get(UUID workspaceId, UUID dataEntityId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(dataEntityId, workspaceId).map(RegistryDataEntityResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.dataEntityNotFound(dataEntityId));
    }
}
