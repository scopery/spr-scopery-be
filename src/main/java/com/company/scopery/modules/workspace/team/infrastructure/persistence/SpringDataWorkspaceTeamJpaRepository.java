package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataWorkspaceTeamJpaRepository
        extends JpaRepository<WorkspaceTeamJpaEntity, UUID>,
                JpaSpecificationExecutor<WorkspaceTeamJpaEntity> {

    boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code);
}
