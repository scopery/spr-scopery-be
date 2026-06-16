package com.company.scopery.modules.workspace.team.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository {

    WorkspaceTeamMember save(WorkspaceTeamMember member);

    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    void delete(UUID teamId, UUID userId);

    List<WorkspaceTeamMember> findByTeamId(UUID teamId);

    List<WorkspaceTeamMember> findByUserId(UUID userId);

    Page<WorkspaceTeamMember> findByTeamIdPageable(UUID teamId, Pageable pageable);
}
