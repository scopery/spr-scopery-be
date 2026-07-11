package com.company.scopery.modules.workspace.orgteam.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface OrgTeamWorkspaceAssignmentRepository {

    OrgTeamWorkspaceAssignment save(OrgTeamWorkspaceAssignment assignment);

    Optional<OrgTeamWorkspaceAssignment> findById(UUID id);

    boolean existsByTeamIdAndWorkspaceIdAndStatus(UUID teamId, UUID workspaceId, String status);

    Optional<OrgTeamWorkspaceAssignment> findByTeamIdAndWorkspaceId(UUID teamId, UUID workspaceId);

    PageResult<OrgTeamWorkspaceAssignment> findAllByTeamId(UUID teamId, PageQuery pageQuery);
}
