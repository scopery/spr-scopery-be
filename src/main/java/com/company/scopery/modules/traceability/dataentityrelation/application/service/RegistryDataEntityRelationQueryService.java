package com.company.scopery.modules.traceability.dataentityrelation.application.service;

import com.company.scopery.modules.traceability.dataentityrelation.application.response.RegistryDataEntityRelationResponse;
import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelationRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryDataEntityRelationQueryService {

    private final RegistryDataEntityRelationRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryDataEntityRelationQueryService(RegistryDataEntityRelationRepository repo,
                                                   TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryDataEntityRelationResponse> list(UUID workspaceId, UUID entityId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByEntityId(entityId).stream()
                .map(RegistryDataEntityRelationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistryDataEntityRelationResponse get(UUID workspaceId, UUID relationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(relationId, workspaceId)
                .map(RegistryDataEntityRelationResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.dataEntityRelationNotFound(relationId));
    }
}
