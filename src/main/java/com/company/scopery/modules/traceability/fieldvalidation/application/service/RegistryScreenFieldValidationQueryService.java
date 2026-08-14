package com.company.scopery.modules.traceability.fieldvalidation.application.service;

import com.company.scopery.modules.traceability.fieldvalidation.application.response.RegistryScreenFieldValidationResponse;
import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidationRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryScreenFieldValidationQueryService {

    private final RegistryScreenFieldValidationRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryScreenFieldValidationQueryService(RegistryScreenFieldValidationRepository repo,
                                                      TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryScreenFieldValidationResponse> listByFieldId(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByFieldId(fieldId).stream()
                .map(RegistryScreenFieldValidationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistryScreenFieldValidationResponse getById(UUID workspaceId, UUID validationId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(validationId, workspaceId)
                .map(RegistryScreenFieldValidationResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.fieldValidationNotFound(validationId));
    }
}
