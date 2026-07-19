package com.company.scopery.modules.trust.sensitiveobject.application.service;

import com.company.scopery.modules.trust.sensitiveobject.application.response.SensitiveObjectRegistryResponse;
import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistryRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SensitiveObjectRegistryQueryService {
    private final SensitiveObjectRegistryRepository repo;
    private final TrustAuthorizationService auth;

    public SensitiveObjectRegistryQueryService(SensitiveObjectRegistryRepository repo, TrustAuthorizationService auth) {
        this.repo = repo;
        this.auth = auth;
    }

    @Transactional(readOnly = true)
    public List<SensitiveObjectRegistryResponse> list(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SensitiveObjectRegistryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SensitiveObjectRegistryResponse get(UUID workspaceId, UUID id) {
        auth.requireView(workspaceId);
        return repo.findById(id).map(SensitiveObjectRegistryResponse::from)
                .orElseThrow(() -> TrustExceptions.sensitiveObjectNotFound(id));
    }
}
