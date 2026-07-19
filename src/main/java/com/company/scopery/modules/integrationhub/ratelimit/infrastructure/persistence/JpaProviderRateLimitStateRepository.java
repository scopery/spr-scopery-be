package com.company.scopery.modules.integrationhub.ratelimit.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.ratelimit.domain.model.ProviderRateLimitState;
import com.company.scopery.modules.integrationhub.ratelimit.domain.model.ProviderRateLimitStateRepository;
import com.company.scopery.modules.integrationhub.ratelimit.infrastructure.mapper.ProviderRateLimitStatePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaProviderRateLimitStateRepository implements ProviderRateLimitStateRepository {
    private final SpringDataProviderRateLimitStateJpaRepository spring;
    private final ProviderRateLimitStatePersistenceMapper mapper;
    public JpaProviderRateLimitStateRepository(SpringDataProviderRateLimitStateJpaRepository spring, ProviderRateLimitStatePersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ProviderRateLimitState save(ProviderRateLimitState s){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(s))); }
    @Override public List<ProviderRateLimitState> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
