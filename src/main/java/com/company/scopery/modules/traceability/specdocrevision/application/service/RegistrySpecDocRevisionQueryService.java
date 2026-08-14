package com.company.scopery.modules.traceability.specdocrevision.application.service;

import com.company.scopery.modules.traceability.specdocrevision.application.response.RegistrySpecDocRevisionResponse;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevisionRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistrySpecDocRevisionQueryService {

    private final RegistrySpecDocRevisionRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistrySpecDocRevisionQueryService(RegistrySpecDocRevisionRepository repo,
                                               TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistrySpecDocRevisionResponse> list(UUID workspaceId, UUID documentId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByDocumentIdOrderByDisplayOrderAsc(documentId)
                .stream()
                .map(RegistrySpecDocRevisionResponse::from)
                .toList();
    }
}
