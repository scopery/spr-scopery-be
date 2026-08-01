package com.company.scopery.modules.traceability.commspec.application.service;

import com.company.scopery.modules.traceability.commspec.application.response.CommunicationSpecResponse;
import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecificationRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommunicationSpecQueryService {

    private final CommunicationSpecificationRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public CommunicationSpecQueryService(CommunicationSpecificationRepository repo,
                                         TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<CommunicationSpecResponse> list(UUID workspaceId, UUID applicationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByApplicationId(applicationId).stream().map(CommunicationSpecResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CommunicationSpecResponse get(UUID workspaceId, UUID applicationId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndApplicationId(id, applicationId)
                .map(CommunicationSpecResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.commSpecNotFound(id));
    }
}
