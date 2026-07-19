package com.company.scopery.modules.traceability.screenfield.application.service;
import com.company.scopery.modules.traceability.screenfield.application.response.RegistryScreenFieldResponse;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RegistryScreenFieldQueryService {
    private final RegistryScreenFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public RegistryScreenFieldQueryService(RegistryScreenFieldRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RegistryScreenFieldResponse> list(UUID workspaceId, UUID screenId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByScreenId(screenId).stream().map(RegistryScreenFieldResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RegistryScreenFieldResponse get(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(fieldId, workspaceId).map(RegistryScreenFieldResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.screenFieldNotFound(fieldId));
    }
}
