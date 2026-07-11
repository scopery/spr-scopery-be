package com.company.scopery.modules.workspace.workspace.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataWorkspaceJpaRepository
        extends JpaRepository<WorkspaceJpaEntity, UUID>,
                JpaSpecificationExecutor<WorkspaceJpaEntity> {

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);

    Optional<WorkspaceJpaEntity> findByCode(String code);

    @Query("SELECT DISTINCT w FROM WorkspaceJpaEntity w " +
           "JOIN WorkspaceMemberJpaEntity m ON w.id = m.workspaceId " +
           "WHERE m.userId = :userId AND m.status = 'ACTIVE' AND w.status != 'ARCHIVED'")
    List<WorkspaceJpaEntity> findActiveByMemberId(@Param("userId") UUID userId);

    List<WorkspaceJpaEntity> findAllByOrganizationIdAndStatus(UUID organizationId, String status);
}
