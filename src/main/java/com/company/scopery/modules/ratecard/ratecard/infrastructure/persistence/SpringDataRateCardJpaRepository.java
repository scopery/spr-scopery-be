package com.company.scopery.modules.ratecard.ratecard.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataRateCardJpaRepository
        extends JpaRepository<RateCardJpaEntity, UUID>, JpaSpecificationExecutor<RateCardJpaEntity> {

    @Query("""
            SELECT COUNT(e) > 0 FROM RateCardJpaEntity e
            WHERE e.scope = :scope AND e.code = :code
              AND ((:organizationId IS NULL AND e.organizationId IS NULL) OR e.organizationId = :organizationId)
              AND ((:workspaceId IS NULL AND e.workspaceId IS NULL) OR e.workspaceId = :workspaceId)
              AND ((:clientId IS NULL AND e.clientId IS NULL) OR e.clientId = :clientId)
              AND ((:projectId IS NULL AND e.projectId IS NULL) OR e.projectId = :projectId)
            """)
    boolean existsByScopeAndCode(@Param("scope") String scope, @Param("organizationId") UUID organizationId,
                                 @Param("workspaceId") UUID workspaceId, @Param("clientId") UUID clientId,
                                 @Param("projectId") UUID projectId, @Param("code") String code);

    List<RateCardJpaEntity> findAllByWorkspaceIdAndIsDefaultTrueAndStatus(UUID workspaceId, String status);

    List<RateCardJpaEntity> findAllByScopeAndStatus(String scope, String status);

    List<RateCardJpaEntity> findAllByScopeAndOrganizationIdAndStatus(String scope, UUID organizationId, String status);

    List<RateCardJpaEntity> findAllByScopeAndWorkspaceIdAndStatus(String scope, UUID workspaceId, String status);
}
