package com.company.scopery.modules.workspace.member.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository {

    WorkspaceMember save(WorkspaceMember member);

    Optional<WorkspaceMember> findById(UUID id);

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    boolean isActiveMember(UUID workspaceId, UUID userId);

    PageResult<WorkspaceMember> findAll(UUID workspaceId, UUID userId, WorkspaceMemberStatus status, PageQuery pageQuery);
}
