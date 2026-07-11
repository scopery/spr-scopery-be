package com.company.scopery.modules.iam.roleassignment.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.roleassignment.domain.enums.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IamRoleAssignmentRepository {

    IamRoleAssignment save(IamRoleAssignment assignment);

    Optional<IamRoleAssignment> findById(UUID id);

    boolean existsActiveAssignment(RoleAssigneeType assigneeType, UUID assigneeId, UUID roleId, UUID workspaceId);

    List<IamRoleAssignment> findActiveByAssigneeId(UUID assigneeId);

    PageResult<IamRoleAssignment> findAll(UUID roleId, UUID assigneeId, RoleAssigneeType assigneeType,
                                     IamRoleAssignmentStatus status, UUID workspaceId, PageQuery pageQuery);
}
