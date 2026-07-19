package com.company.scopery.modules.ratecard.costrole.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCostRoleJpaRepository
        extends JpaRepository<CostRoleJpaEntity, UUID>, JpaSpecificationExecutor<CostRoleJpaEntity> {

    Optional<CostRoleJpaEntity> findByCode(String code);

    @Query("""
            SELECT COUNT(e) > 0 FROM CostRoleJpaEntity e
            WHERE e.scope = :scope AND e.code = :code
              AND ((:organizationId IS NULL AND e.organizationId IS NULL) OR e.organizationId = :organizationId)
              AND ((:workspaceId IS NULL AND e.workspaceId IS NULL) OR e.workspaceId = :workspaceId)
            """)
    boolean existsByScopeAndCode(@Param("scope") String scope,
                                 @Param("organizationId") UUID organizationId,
                                 @Param("workspaceId") UUID workspaceId,
                                 @Param("code") String code);

    @Query(value = "SELECT COUNT(1) > 0 FROM rate_card_line l WHERE l.cost_role_id = :costRoleId", nativeQuery = true)
    boolean existsLineByCostRoleId(@Param("costRoleId") UUID costRoleId);
}
