package com.company.scopery.modules.traceability.componentfield.application.service;

import com.company.scopery.modules.traceability.componentfield.application.response.RegistryComponentFieldResponse;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class RegistryComponentFieldQueryService {

    private final RegistryComponentFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryComponentFieldQueryService(RegistryComponentFieldRepository repo,
                                               TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryComponentFieldResponse> list(UUID workspaceId, UUID componentId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByComponentIdOrderByDisplayOrderAsc(componentId).stream()
                .map(RegistryComponentFieldResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RegistryComponentFieldResponse get(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(fieldId, workspaceId)
                .map(RegistryComponentFieldResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.componentFieldNotFound(fieldId));
    }
}
