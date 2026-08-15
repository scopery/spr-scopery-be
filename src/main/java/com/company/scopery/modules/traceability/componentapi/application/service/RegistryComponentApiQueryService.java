package com.company.scopery.modules.traceability.componentapi.application.service;

import com.company.scopery.modules.traceability.componentapi.application.response.RegistryComponentApiResponse;
import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApiRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryComponentApiQueryService {

    private final RegistryComponentApiRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryComponentApiQueryService(RegistryComponentApiRepository repo,
                                             TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryComponentApiResponse> list(UUID workspaceId, UUID componentId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByComponentId(componentId).stream().map(RegistryComponentApiResponse::from).toList();
    }
}
