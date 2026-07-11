package com.company.scopery.modules.workspace.team.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.team.domain.enums.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.valueobject.TeamCode;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {

    WorkspaceTeam save(WorkspaceTeam team);

    Optional<WorkspaceTeam> findById(UUID id);

    boolean existsByWorkspaceIdAndCode(UUID workspaceId, TeamCode code);

    PageResult<WorkspaceTeam> findAll(UUID workspaceId, TeamStatus status, PageQuery pageQuery);
}
