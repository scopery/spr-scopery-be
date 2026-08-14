package com.company.scopery.modules.traceability.componentoption.application.service;

import com.company.scopery.modules.traceability.componentoption.application.response.RegistryComponentOptionResponse;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOptionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryComponentOptionQueryService {

    private final RegistryComponentOptionRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryComponentOptionQueryService(RegistryComponentOptionRepository repo,
                                                TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryComponentOptionResponse> listByComponent(UUID workspaceId, UUID componentId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByComponentId(componentId).stream()
                .map(RegistryComponentOptionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistryComponentOptionResponse get(UUID workspaceId, UUID optionId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(optionId, workspaceId)
                .map(RegistryComponentOptionResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.componentOptionNotFound(optionId));
    }
}
