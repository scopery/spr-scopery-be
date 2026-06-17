package com.company.scopery.modules.workspace.joinrequest.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataWorkspaceJoinRequestJpaRepository
        extends JpaRepository<WorkspaceJoinRequestJpaEntity, UUID> {

    @Query("SELECT r FROM WorkspaceJoinRequestJpaEntity r " +
           "WHERE r.workspaceId = :workspaceId AND r.requestedByUserId = :userId AND r.status = 'PENDING'")
    Optional<WorkspaceJoinRequestJpaEntity> findPendingByWorkspaceAndUser(
            @Param("workspaceId") UUID workspaceId,
            @Param("userId") UUID userId);

    List<WorkspaceJoinRequestJpaEntity> findByWorkspaceIdAndStatus(UUID workspaceId, String status);

    List<WorkspaceJoinRequestJpaEntity> findByWorkspaceId(UUID workspaceId);
}
