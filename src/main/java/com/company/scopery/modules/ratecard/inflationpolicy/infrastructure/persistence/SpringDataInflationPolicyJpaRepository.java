package com.company.scopery.modules.ratecard.inflationpolicy.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate; import java.util.List; import java.util.UUID;

public interface SpringDataInflationPolicyJpaRepository
        extends JpaRepository<InflationPolicyJpaEntity, UUID>, JpaSpecificationExecutor<InflationPolicyJpaEntity> {

    @Query("""
            SELECT COUNT(e) > 0 FROM InflationPolicyJpaEntity e
            WHERE e.scope = :scope AND e.code = :code
              AND ((:organizationId IS NULL AND e.organizationId IS NULL) OR e.organizationId = :organizationId)
              AND ((:workspaceId IS NULL AND e.workspaceId IS NULL) OR e.workspaceId = :workspaceId)
            """)
    boolean existsByScopeAndCode(@Param("scope") String scope, @Param("organizationId") UUID organizationId,
                                 @Param("workspaceId") UUID workspaceId, @Param("code") String code);

    @Query("""
            SELECT e FROM InflationPolicyJpaEntity e
            WHERE e.scope = :scope AND e.status = 'ACTIVE'
              AND ((:organizationId IS NULL AND e.organizationId IS NULL) OR e.organizationId = :organizationId)
              AND ((:workspaceId IS NULL AND e.workspaceId IS NULL) OR e.workspaceId = :workspaceId)
              AND e.effectiveFrom <= :date
              AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date)
            """)
    List<InflationPolicyJpaEntity> findActiveCovering(@Param("scope") String scope,
                                                      @Param("organizationId") UUID organizationId,
                                                      @Param("workspaceId") UUID workspaceId,
                                                      @Param("date") LocalDate date);
}
