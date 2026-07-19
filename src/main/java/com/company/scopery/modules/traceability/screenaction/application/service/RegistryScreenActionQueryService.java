package com.company.scopery.modules.traceability.screenaction.application.service;
import com.company.scopery.modules.traceability.screenaction.application.response.RegistryScreenActionResponse;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenActionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryScreenActionQueryService {
    private final RegistryScreenActionRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryScreenActionQueryService(RegistryScreenActionRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryScreenActionResponse> list(UUID workspaceId, UUID screenId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByScreenId(screenId).stream().map(RegistryScreenActionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryScreenActionResponse get(UUID workspaceId, UUID actionId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(actionId, workspaceId).map(RegistryScreenActionResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.screenActionNotFound(actionId));
    }
}
