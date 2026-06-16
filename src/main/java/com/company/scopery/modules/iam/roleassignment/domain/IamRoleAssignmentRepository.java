package com.company.scopery.modules.iam.roleassignment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IamRoleAssignmentRepository {

    IamRoleAssignment save(IamRoleAssignment assignment);

    Optional<IamRoleAssignment> findById(UUID id);

    boolean existsActiveAssignment(RoleAssigneeType assigneeType, UUID assigneeId, UUID roleId, UUID workspaceId);

    List<IamRoleAssignment> findActiveByAssigneeId(UUID assigneeId);

    Page<IamRoleAssignment> findAll(UUID roleId, UUID assigneeId, RoleAssigneeType assigneeType,
                                     IamRoleAssignmentStatus status, UUID workspaceId, Pageable pageable);
}
