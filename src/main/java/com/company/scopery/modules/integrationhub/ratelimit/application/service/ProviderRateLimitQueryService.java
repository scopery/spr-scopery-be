package com.company.scopery.modules.integrationhub.ratelimit.application.service;
import com.company.scopery.modules.integrationhub.ratelimit.application.response.ProviderRateLimitStateResponse;
import com.company.scopery.modules.integrationhub.ratelimit.domain.model.ProviderRateLimitStateRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ProviderRateLimitQueryService {
    private final ProviderRateLimitStateRepository repo;
    private final IntegrationAuthorizationService auth;
    public ProviderRateLimitQueryService(ProviderRateLimitStateRepository repo, IntegrationAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<ProviderRateLimitStateResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(ProviderRateLimitStateResponse::from).toList();
    }
}
