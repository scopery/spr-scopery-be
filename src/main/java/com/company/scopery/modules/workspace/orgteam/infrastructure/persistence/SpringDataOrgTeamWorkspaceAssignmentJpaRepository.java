package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataOrgTeamWorkspaceAssignmentJpaRepository
        extends JpaRepository<OrgTeamWorkspaceAssignmentJpaEntity, UUID> {

    boolean existsByTeamIdAndWorkspaceIdAndStatus(UUID teamId, UUID workspaceId, String status);

    Optional<OrgTeamWorkspaceAssignmentJpaEntity> findByTeamIdAndWorkspaceId(UUID teamId, UUID workspaceId);

    Page<OrgTeamWorkspaceAssignmentJpaEntity> findAllByTeamId(UUID teamId, Pageable pageable);
}
