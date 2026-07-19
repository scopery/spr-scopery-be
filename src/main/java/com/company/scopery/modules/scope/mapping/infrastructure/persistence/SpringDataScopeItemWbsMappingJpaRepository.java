package com.company.scopery.modules.scope.mapping.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataScopeItemWbsMappingJpaRepository extends JpaRepository<ScopeItemWbsMappingJpaEntity, UUID> {
    Optional<ScopeItemWbsMappingJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopeItemWbsMappingJpaEntity> findByScopeItemIdAndArchivedAtIsNullOrderByCreatedAtDesc(UUID scopeItemId);
    long countByProjectIdAndArchivedAtIsNull(UUID projectId);
}
