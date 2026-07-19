package com.company.scopery.modules.integrationhub.ratelimit.domain.model;
import java.util.List; import java.util.UUID;
public interface ProviderRateLimitStateRepository {
    ProviderRateLimitState save(ProviderRateLimitState s);
    List<ProviderRateLimitState> findByWorkspaceId(UUID workspaceId);
}
