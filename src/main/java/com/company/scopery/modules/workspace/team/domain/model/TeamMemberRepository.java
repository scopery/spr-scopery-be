package com.company.scopery.modules.workspace.team.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository {

    WorkspaceTeamMember save(WorkspaceTeamMember member);

    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    void delete(UUID teamId, UUID userId);

    List<WorkspaceTeamMember> findByTeamId(UUID teamId);

    List<WorkspaceTeamMember> findByUserId(UUID userId);

    PageResult<WorkspaceTeamMember> findByTeamIdPageable(UUID teamId, PageQuery pageQuery);
}
