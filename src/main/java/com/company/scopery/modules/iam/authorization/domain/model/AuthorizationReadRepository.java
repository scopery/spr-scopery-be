package com.company.scopery.modules.iam.authorization.domain.model;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.user.domain.model.IamUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AuthorizationReadRepository {

    Optional<IamUser> findUserById(UUID userId);

    Optional<IamRight> findRightByCode(IamRightCode code);

    Optional<IamAuthResource> findResourceById(UUID resourceId);

    Optional<IamAuthResource> findResourceByRefIdAndType(UUID refId, IamResourceType resourceType);

    List<IamAccessGrant> findActiveGrantsBySubjectsAndResource(
            List<IamSubjectType> subjectTypes, List<UUID> subjectIds, UUID resourceId);

    Set<UUID> findGrantIdsHavingAnyPermissionAction(List<UUID> grantIds, List<UUID> permissionActionIds);

    Set<UUID> findGrantIdsHavingRight(List<UUID> grantIds, UUID rightId);

    Optional<IamPermission> findPermissionByCode(IamPermissionCode code);

    Optional<IamPermissionActionDefinition> findPermissionActionByPermissionIdAndCode(
            UUID permissionId, String actionCode);

    List<IamPermissionActionDefinition> findPermissionActionsByRightIds(List<UUID> rightIds);

    Optional<IamPermission> findPermissionById(UUID permissionId);

    List<IamRoleAssignment> findActiveRoleAssignmentsByAssigneeId(UUID assigneeId);

    List<UUID> findWorkspaceTeamIdsByUserId(UUID userId);

    List<UUID> findOrgTeamIdsByUserId(UUID userId);
}
