package com.company.scopery.modules.iam.roleassignment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataIamRoleAssignmentJpaRepository
        extends JpaRepository<IamRoleAssignmentJpaEntity, UUID>,
                JpaSpecificationExecutor<IamRoleAssignmentJpaEntity> {

    @Query("""
            SELECT COUNT(a) > 0 FROM IamRoleAssignmentJpaEntity a
            WHERE a.assigneeType = :assigneeType
              AND a.assigneeId = :assigneeId
              AND a.roleId = :roleId
              AND a.status = 'ACTIVE'
              AND (:workspaceId IS NULL AND a.workspaceId IS NULL
                   OR a.workspaceId = :workspaceId)
            """)
    boolean existsActiveAssignment(@Param("assigneeType") String assigneeType,
                                   @Param("assigneeId") UUID assigneeId,
                                   @Param("roleId") UUID roleId,
                                   @Param("workspaceId") UUID workspaceId);

    @Query("""
            SELECT a FROM IamRoleAssignmentJpaEntity a
            WHERE a.assigneeId = :assigneeId AND a.status = 'ACTIVE'
            """)
    List<IamRoleAssignmentJpaEntity> findActiveByAssigneeId(@Param("assigneeId") UUID assigneeId);
}
