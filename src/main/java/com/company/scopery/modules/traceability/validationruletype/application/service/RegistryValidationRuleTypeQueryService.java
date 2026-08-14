package com.company.scopery.modules.traceability.validationruletype.application.service;

import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.validationruletype.application.response.RegistryValidationRuleTypeResponse;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryValidationRuleTypeQueryService {

    private final RegistryValidationRuleTypeRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryValidationRuleTypeQueryService(RegistryValidationRuleTypeRepository repo,
                                                   TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryValidationRuleTypeResponse> listAccessible(UUID workspaceId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findAllAccessible(workspaceId).stream()
                .map(RegistryValidationRuleTypeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistryValidationRuleTypeResponse get(UUID workspaceId, UUID id) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndAccessible(id, workspaceId)
                .map(RegistryValidationRuleTypeResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.validationRuleTypeNotFound(id));
    }
}
