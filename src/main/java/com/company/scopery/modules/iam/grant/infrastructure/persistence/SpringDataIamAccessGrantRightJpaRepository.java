package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SpringDataIamAccessGrantRightJpaRepository
        extends JpaRepository<IamAccessGrantRightJpaEntity, IamAccessGrantRightKey> {

    boolean existsByGrantIdAndRightId(UUID grantId, UUID rightId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM IamAccessGrantRightJpaEntity e WHERE e.grantId = :grantId AND e.rightId = :rightId")
    int deleteByGrantIdAndRightId(@Param("grantId") UUID grantId, @Param("rightId") UUID rightId);

    List<IamAccessGrantRightJpaEntity> findByGrantId(UUID grantId);

    @Query("SELECT gr.grantId FROM IamAccessGrantRightJpaEntity gr WHERE gr.grantId IN :grantIds AND gr.rightId = :rightId")
    Set<UUID> findGrantIdsHavingRight(@Param("grantIds") List<UUID> grantIds, @Param("rightId") UUID rightId);
}
