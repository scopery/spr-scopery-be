package com.company.scopery.modules.scope.scopepackage.infrastructure.persistence;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface SpringDataScopePackageJpaRepository extends JpaRepository<ScopePackageJpaEntity, UUID> {
    Optional<ScopePackageJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopePackageJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update ScopePackageJpaEntity e set e.currentFlag = false, e.updatedAt = CURRENT_TIMESTAMP where e.projectId = :projectId and e.currentFlag = true")
    int clearCurrentFlag(@Param("projectId") UUID projectId);
}
