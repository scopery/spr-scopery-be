package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SpringDataIamAccessGrantPermissionActionJpaRepository
        extends JpaRepository<IamAccessGrantPermissionActionJpaEntity, IamAccessGrantPermissionActionKey> {

    boolean existsByGrantIdAndPermissionActionId(UUID grantId, UUID permissionActionId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            DELETE FROM IamAccessGrantPermissionActionJpaEntity e
            WHERE e.grantId = :grantId AND e.permissionActionId = :permissionActionId
            """)
    int deleteByGrantIdAndPermissionActionId(@Param("grantId") UUID grantId,
                                             @Param("permissionActionId") UUID permissionActionId);

    List<IamAccessGrantPermissionActionJpaEntity> findByGrantId(UUID grantId);

    @Query("""
            SELECT ga.grantId
            FROM IamAccessGrantPermissionActionJpaEntity ga
            WHERE ga.grantId IN :grantIds
              AND ga.permissionActionId = :permissionActionId
            """)
    Set<UUID> findGrantIdsHavingPermissionAction(@Param("grantIds") List<UUID> grantIds,
                                                 @Param("permissionActionId") UUID permissionActionId);

    @Query("""
            SELECT DISTINCT ga.grantId
            FROM IamAccessGrantPermissionActionJpaEntity ga
            WHERE ga.grantId IN :grantIds
              AND ga.permissionActionId IN :permissionActionIds
            """)
    Set<UUID> findGrantIdsHavingAnyPermissionAction(@Param("grantIds") List<UUID> grantIds,
                                                    @Param("permissionActionIds") List<UUID> permissionActionIds);
}
