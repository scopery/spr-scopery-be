package com.company.scopery.modules.traceability.appcomponent.infrastructure.persistence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface SpringDataRegistryAppComponentJpaRepository extends JpaRepository<RegistryAppComponentJpaEntity, UUID> {
    List<RegistryAppComponentJpaEntity> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
    Optional<RegistryAppComponentJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Query("""
            SELECT c FROM RegistryAppComponentJpaEntity c
            WHERE c.id IN (
                SELECT sc.id.componentId FROM ScreenComponentJpaEntity sc
                WHERE sc.id.screenId = :screenId
            )
            AND (:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY c.name ASC
            """)
    Page<RegistryAppComponentJpaEntity> searchByScreenId(
            @Param("screenId") UUID screenId,
            @Param("query") String query,
            Pageable pageable);
}
