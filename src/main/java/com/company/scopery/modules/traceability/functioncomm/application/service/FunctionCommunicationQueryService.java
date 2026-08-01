package com.company.scopery.modules.traceability.functioncomm.application.service;

import com.company.scopery.modules.traceability.functioncomm.application.response.FunctionCommunicationResponse;
import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunicationRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FunctionCommunicationQueryService {

    private final FunctionCommunicationRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public FunctionCommunicationQueryService(FunctionCommunicationRepository repo,
                                             TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<FunctionCommunicationResponse> listByFunction(UUID projectId, UUID functionalItemId) {
        authorization.requireView(projectId);
        return repo.findByFunctionId(functionalItemId).stream().map(FunctionCommunicationResponse::from).toList();
    }
}
