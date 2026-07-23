package com.company.scopery.modules.traceability.funcitemprop.application.service;

import com.company.scopery.modules.traceability.funcitemprop.application.response.FunctionalItemCustomPropertyResponse;
import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomPropertyRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FunctionalItemCustomPropertyQueryService {

    private final FunctionalItemCustomPropertyRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public FunctionalItemCustomPropertyQueryService(
            FunctionalItemCustomPropertyRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<FunctionalItemCustomPropertyResponse> list(UUID functionalItemId, UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByFunctionalItemId(functionalItemId)
                .stream().map(FunctionalItemCustomPropertyResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FunctionalItemCustomPropertyResponse get(UUID id, UUID functionalItemId, UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByIdAndFunctionalItemId(id, functionalItemId)
                .map(FunctionalItemCustomPropertyResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.funcItemCustomPropNotFound(id));
    }
}
