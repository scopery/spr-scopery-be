package com.company.scopery.modules.traceability.functionalitem.application.service;

import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemResponse;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FunctionalItemQueryService {

    private final FunctionalItemRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public FunctionalItemQueryService(FunctionalItemRepository repo,
                                      TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<FunctionalItemResponse> listByProject(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream()
                .map(FunctionalItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FunctionalItemResponse> listByModule(UUID projectId, UUID moduleId) {
        authorization.requireView(projectId);
        return repo.findByProjectIdAndModuleId(projectId, moduleId).stream()
                .map(FunctionalItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FunctionalItemResponse get(UUID id, UUID projectId) {
        authorization.requireView(projectId);
        return FunctionalItemResponse.from(
                repo.findByIdAndProjectId(id, projectId)
                        .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(id))
        );
    }
}
