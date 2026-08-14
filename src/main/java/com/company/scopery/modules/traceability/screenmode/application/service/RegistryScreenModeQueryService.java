package com.company.scopery.modules.traceability.screenmode.application.service;

import com.company.scopery.modules.traceability.screenmode.application.response.RegistryScreenModeResponse;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenModeRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryScreenModeQueryService {

    private final RegistryScreenModeRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryScreenModeQueryService(RegistryScreenModeRepository repo,
                                          TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryScreenModeResponse> list(UUID workspaceId, UUID screenId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByScreenId(screenId).stream().map(RegistryScreenModeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RegistryScreenModeResponse get(UUID workspaceId, UUID modeId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByIdAndWorkspaceId(modeId, workspaceId)
                .map(RegistryScreenModeResponse::from)
                .orElseThrow(() -> TraceabilityExceptions.screenModeNotFound(modeId));
    }
}
