package com.company.scopery.modules.workspace.member.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository {

    WorkspaceMember save(WorkspaceMember member);

    Optional<WorkspaceMember> findById(UUID id);

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    boolean isActiveMember(UUID workspaceId, UUID userId);

    Page<WorkspaceMember> findAll(UUID workspaceId, UUID userId, WorkspaceMemberStatus status, Pageable pageable);
}
