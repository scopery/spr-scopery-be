package com.company.scopery.modules.traceability.nonfunctionalitem.application.service;

import com.company.scopery.modules.traceability.nonfunctionalitem.application.response.NonFunctionalItemResponse;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItemRepository;
import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTargetRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NonFunctionalItemQueryService {

    private final NonFunctionalItemRepository repository;
    private final NfrScopeTargetRepository scopeTargetRepo;
    private final TraceabilityAuthorizationService authorization;

    public NonFunctionalItemQueryService(
            NonFunctionalItemRepository repository,
            NfrScopeTargetRepository scopeTargetRepo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repository = repository;
        this.scopeTargetRepo = scopeTargetRepo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<NonFunctionalItemResponse> listByProject(UUID projectId) {
        authorization.requireView(projectId);
        return repository.findByProjectId(projectId)
                .stream()
                .map(NonFunctionalItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NonFunctionalItemResponse> listByTarget(UUID projectId, UUID targetId) {
        authorization.requireView(projectId);
        Set<UUID> nfrIds = scopeTargetRepo.findByTargetId(targetId).stream()
                .map(t -> t.nfrId())
                .collect(Collectors.toSet());
        if (nfrIds.isEmpty()) return List.of();
        return repository.findByProjectIdAndIdIn(projectId, nfrIds)
                .stream()
                .map(NonFunctionalItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NonFunctionalItemResponse get(UUID id, UUID projectId) {
        authorization.requireView(projectId);
        return repository.findByIdAndProjectId(id, projectId)
                .map(NonFunctionalItemResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.nonFunctionalItemNotFound(id));
    }
}
