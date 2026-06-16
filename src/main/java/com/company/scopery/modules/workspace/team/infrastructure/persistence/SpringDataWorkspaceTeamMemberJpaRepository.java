package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataWorkspaceTeamMemberJpaRepository
        extends JpaRepository<WorkspaceTeamMemberJpaEntity, WorkspaceTeamMemberKey> {

    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    List<WorkspaceTeamMemberJpaEntity> findByTeamId(UUID teamId);

    List<WorkspaceTeamMemberJpaEntity> findByUserId(UUID userId);

    Page<WorkspaceTeamMemberJpaEntity> findByTeamId(UUID teamId, Pageable pageable);

    void deleteByTeamIdAndUserId(UUID teamId, UUID userId);
}
