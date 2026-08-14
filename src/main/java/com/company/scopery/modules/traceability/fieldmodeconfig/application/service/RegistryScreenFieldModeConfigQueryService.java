package com.company.scopery.modules.traceability.fieldmodeconfig.application.service;

import com.company.scopery.modules.traceability.fieldmodeconfig.application.response.RegistryScreenFieldModeConfigResponse;
import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfigRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryScreenFieldModeConfigQueryService {

    private final RegistryScreenFieldModeConfigRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryScreenFieldModeConfigQueryService(RegistryScreenFieldModeConfigRepository repo,
                                                      TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryScreenFieldModeConfigResponse> listByFieldId(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByFieldId(fieldId).stream()
                .map(RegistryScreenFieldModeConfigResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistryScreenFieldModeConfigResponse getById(UUID workspaceId, UUID configId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(configId, workspaceId)
                .map(RegistryScreenFieldModeConfigResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.fieldModeConfigNotFound(configId));
    }
}
