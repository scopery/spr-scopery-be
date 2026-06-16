package com.company.scopery.modules.workspace.team.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {

    WorkspaceTeam save(WorkspaceTeam team);

    Optional<WorkspaceTeam> findById(UUID id);

    boolean existsByWorkspaceIdAndCode(UUID workspaceId, TeamCode code);

    Page<WorkspaceTeam> findAll(UUID workspaceId, TeamStatus status, Pageable pageable);
}
