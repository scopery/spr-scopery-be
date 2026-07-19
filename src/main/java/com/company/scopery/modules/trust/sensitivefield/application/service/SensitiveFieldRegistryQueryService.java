package com.company.scopery.modules.trust.sensitivefield.application.service;

import com.company.scopery.modules.trust.sensitivefield.application.response.SensitiveFieldRegistryResponse;
import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistryRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SensitiveFieldRegistryQueryService {
    private final SensitiveFieldRegistryRepository repo;
    private final TrustAuthorizationService auth;

    public SensitiveFieldRegistryQueryService(SensitiveFieldRegistryRepository repo, TrustAuthorizationService auth) {
        this.repo = repo;
        this.auth = auth;
    }

    @Transactional(readOnly = true)
    public List<SensitiveFieldRegistryResponse> list(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SensitiveFieldRegistryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SensitiveFieldRegistryResponse get(UUID workspaceId, UUID id) {
        auth.requireView(workspaceId);
        return repo.findById(id).map(SensitiveFieldRegistryResponse::from)
                .orElseThrow(() -> TrustExceptions.sensitiveFieldNotFound(id));
    }
}
