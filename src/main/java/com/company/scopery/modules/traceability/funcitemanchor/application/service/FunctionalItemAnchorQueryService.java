package com.company.scopery.modules.traceability.funcitemanchor.application.service;

import com.company.scopery.modules.traceability.funcitemanchor.application.response.FunctionalItemAnchorResponse;
import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchorRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FunctionalItemAnchorQueryService {

    private final FunctionalItemAnchorRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public FunctionalItemAnchorQueryService(
            FunctionalItemAnchorRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<FunctionalItemAnchorResponse> list(UUID functionalItemId, UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByFunctionalItemId(functionalItemId)
                .stream().map(FunctionalItemAnchorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FunctionalItemAnchorResponse get(UUID id, UUID functionalItemId, UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByIdAndFunctionalItemId(id, functionalItemId)
                .map(FunctionalItemAnchorResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.funcItemAnchorNotFound(id));
    }

    @Transactional(readOnly = true)
    public List<FunctionalItemAnchorResponse> listByProject(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(FunctionalItemAnchorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FunctionalItemAnchorResponse> listByNode(UUID projectId, String nodeType, UUID nodeId) {
        authorization.requireView(projectId);
        return repo.findByNodeTypeAndNodeIdAndProjectId(nodeType, nodeId, projectId)
                .stream().map(FunctionalItemAnchorResponse::from).toList();
    }
}
