package com.company.scopery.modules.workspace.member.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataWorkspaceMemberJpaRepository
        extends JpaRepository<WorkspaceMemberJpaEntity, UUID>,
                JpaSpecificationExecutor<WorkspaceMemberJpaEntity> {

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    Optional<WorkspaceMemberJpaEntity> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    boolean existsByWorkspaceIdAndUserIdAndStatus(UUID workspaceId, UUID userId, String status);
}
