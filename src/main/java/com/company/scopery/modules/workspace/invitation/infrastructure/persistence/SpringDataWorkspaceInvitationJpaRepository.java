package com.company.scopery.modules.workspace.invitation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataWorkspaceInvitationJpaRepository
        extends JpaRepository<WorkspaceInvitationJpaEntity, UUID> {

    Optional<WorkspaceInvitationJpaEntity> findByInvitationCodeHash(String codeHash);

    List<WorkspaceInvitationJpaEntity> findByWorkspaceId(UUID workspaceId);
}
