package com.company.scopery.modules.iam.permission.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataIamPermissionActionDefinitionJpaRepository
        extends JpaRepository<IamPermissionActionDefinitionJpaEntity, UUID> {

    Optional<IamPermissionActionDefinitionJpaEntity> findByPermissionIdAndActionCode(UUID permissionId,
                                                                                     String actionCode);

    boolean existsByPermissionIdAndActionCode(UUID permissionId, String actionCode);

    List<IamPermissionActionDefinitionJpaEntity> findByPermissionIdOrderByActionCodeAsc(UUID permissionId);

    List<IamPermissionActionDefinitionJpaEntity> findByPermissionIdInOrderByActionCodeAsc(List<UUID> permissionIds);

    List<IamPermissionActionDefinitionJpaEntity> findByRightIdIn(List<UUID> rightIds);
}
