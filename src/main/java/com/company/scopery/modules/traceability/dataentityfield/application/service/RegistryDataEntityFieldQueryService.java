package com.company.scopery.modules.traceability.dataentityfield.application.service;

import com.company.scopery.modules.traceability.dataentityfield.application.response.RegistryDataEntityFieldResponse;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryDataEntityFieldQueryService {

    private final RegistryDataEntityFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryDataEntityFieldQueryService(RegistryDataEntityFieldRepository repo,
                                               TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryDataEntityFieldResponse> list(UUID workspaceId, UUID entityId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByEntityId(entityId).stream().map(RegistryDataEntityFieldResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RegistryDataEntityFieldResponse get(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(fieldId, workspaceId)
                .map(RegistryDataEntityFieldResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.dataEntityFieldNotFound(fieldId));
    }
}
