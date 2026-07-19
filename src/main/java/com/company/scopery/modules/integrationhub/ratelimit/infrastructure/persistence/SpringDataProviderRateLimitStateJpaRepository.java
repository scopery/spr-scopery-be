package com.company.scopery.modules.integrationhub.ratelimit.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataProviderRateLimitStateJpaRepository extends JpaRepository<ProviderRateLimitStateJpaEntity, UUID> {
    List<ProviderRateLimitStateJpaEntity> findByWorkspaceId(UUID workspaceId);
}
