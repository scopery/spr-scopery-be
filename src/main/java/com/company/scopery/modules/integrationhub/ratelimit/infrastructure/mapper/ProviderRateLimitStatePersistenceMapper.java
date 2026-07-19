package com.company.scopery.modules.integrationhub.ratelimit.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.ratelimit.domain.model.ProviderRateLimitState;
import com.company.scopery.modules.integrationhub.ratelimit.infrastructure.persistence.ProviderRateLimitStateJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProviderRateLimitStatePersistenceMapper {
    public ProviderRateLimitStateJpaEntity toJpaEntity(ProviderRateLimitState d) {
        ProviderRateLimitStateJpaEntity e = new ProviderRateLimitStateJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConnectionId(d.connectionId());
        e.setProviderCode(d.providerCode()); e.setStatus(d.status()); e.setLimitName(d.limitName());
        e.setRemainingCount(d.remainingCount()); e.setResetAt(d.resetAt()); e.setBackoffUntil(d.backoffUntil());
        e.setLastUpdatedAt(d.lastUpdatedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ProviderRateLimitState toDomain(ProviderRateLimitStateJpaEntity e) {
        return new ProviderRateLimitState(e.getId(), e.getWorkspaceId(), e.getConnectionId(), e.getProviderCode(),
                e.getStatus(), e.getLimitName(), e.getRemainingCount(), e.getResetAt(), e.getBackoffUntil(),
                e.getLastUpdatedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
