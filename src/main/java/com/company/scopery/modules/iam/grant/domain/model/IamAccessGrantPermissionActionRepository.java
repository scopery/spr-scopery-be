package com.company.scopery.modules.iam.grant.domain.model;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IamAccessGrantPermissionActionRepository {

    IamAccessGrantPermissionAction save(IamAccessGrantPermissionAction grantAction);

    boolean existsByGrantIdAndPermissionActionId(UUID grantId, UUID permissionActionId);

    void deleteByGrantIdAndPermissionActionId(UUID grantId, UUID permissionActionId);

    List<IamAccessGrantPermissionAction> findByGrantId(UUID grantId);

    Set<UUID> findGrantIdsHavingPermissionAction(List<UUID> grantIds, UUID permissionActionId);

    Set<UUID> findGrantIdsHavingAnyPermissionAction(List<UUID> grantIds, List<UUID> permissionActionIds);
}
