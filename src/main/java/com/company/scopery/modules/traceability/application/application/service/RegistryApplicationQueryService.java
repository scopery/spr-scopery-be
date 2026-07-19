package com.company.scopery.modules.traceability.application.application.service;
import com.company.scopery.modules.traceability.application.application.response.RegistryApplicationResponse;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryApplicationQueryService {
    private final RegistryApplicationRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryApplicationQueryService(RegistryApplicationRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryApplicationResponse> list(UUID workspaceId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(RegistryApplicationResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryApplicationResponse get(UUID workspaceId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(id, workspaceId).map(RegistryApplicationResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.applicationNotFound(id));
    }
}
