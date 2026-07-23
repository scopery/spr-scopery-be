package com.company.scopery.modules.traceability.businessrule.application.service;

import com.company.scopery.modules.traceability.businessrule.application.response.BusinessRuleResponse;
import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BusinessRuleQueryService {

    private final BusinessRuleRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public BusinessRuleQueryService(
            BusinessRuleRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<BusinessRuleResponse> listByFunctionalItem(UUID functionalItemId, UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByFunctionalItemId(functionalItemId)
                .stream()
                .map(BusinessRuleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessRuleResponse get(UUID id, UUID functionalItemId, UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByIdAndFunctionalItemId(id, functionalItemId)
                .map(BusinessRuleResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.businessRuleNotFound(id));
    }
}
