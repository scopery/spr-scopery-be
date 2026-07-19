package com.company.scopery.modules.aiagent.deployment.infrastructure.persistence;

import com.company.scopery.modules.aiagent.deployment.infrastructure.persistence.entity.ModelDeploymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataModelDeploymentJpaRepository
        extends JpaRepository<ModelDeploymentJpaEntity, UUID>, JpaSpecificationExecutor<ModelDeploymentJpaEntity> {

    boolean existsByModelIdAndCode(UUID modelId, String code);

    boolean existsByModelIdAndStatus(UUID modelId, String status);

    @Query("""
           SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
           FROM ModelDeploymentJpaEntity d
           WHERE d.status = 'ACTIVE'
             AND d.modelId IN (
                 SELECT m.id FROM AiModelJpaEntity m WHERE m.providerId = :providerId
             )
           """)
    boolean existsActiveByProviderId(@Param("providerId") UUID providerId);

    List<ModelDeploymentJpaEntity> findByStatus(String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE ModelDeploymentJpaEntity e
           SET e.isDefault = false,
               e.updatedAt = CURRENT_TIMESTAMP
           WHERE e.modelId = :modelId
             AND e.environment = :environment
           """)
    int clearAllDefaultFlags(@Param("modelId") UUID modelId, @Param("environment") String environment);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE ModelDeploymentJpaEntity e
           SET e.isDefault = false,
               e.updatedAt = CURRENT_TIMESTAMP
           WHERE e.modelId = :modelId
             AND e.environment = :environment
             AND e.id <> :excludeId
           """)
    int clearOtherDefaultFlags(@Param("modelId") UUID modelId, @Param("environment") String environment,
                               @Param("excludeId") UUID excludeId);
}
