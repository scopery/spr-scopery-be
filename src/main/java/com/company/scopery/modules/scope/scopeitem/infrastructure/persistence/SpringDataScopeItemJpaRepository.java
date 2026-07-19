package com.company.scopery.modules.scope.scopeitem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataScopeItemJpaRepository extends JpaRepository<ScopeItemJpaEntity, UUID> {
    Optional<ScopeItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopeItemJpaEntity> findByScopePackageIdOrderBySortOrderAscCreatedAtAsc(UUID scopePackageId);
    List<ScopeItemJpaEntity> findByProjectIdOrderByCreatedAtAsc(UUID projectId);
}
