package com.company.scopery.modules.iam.permission.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IamPermissionActionDefinitionRepository {

    IamPermissionActionDefinition save(IamPermissionActionDefinition action);

    Optional<IamPermissionActionDefinition> findById(UUID id);

    Optional<IamPermissionActionDefinition> findByPermissionIdAndActionCode(UUID permissionId, String actionCode);

    boolean existsByPermissionIdAndActionCode(UUID permissionId, String actionCode);

    List<IamPermissionActionDefinition> findByPermissionId(UUID permissionId);

    List<IamPermissionActionDefinition> findByPermissionIds(List<UUID> permissionIds);

    List<IamPermissionActionDefinition> findByRightIds(List<UUID> rightIds);
}
