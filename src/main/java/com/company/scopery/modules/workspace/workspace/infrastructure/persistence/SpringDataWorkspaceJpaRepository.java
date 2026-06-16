package com.company.scopery.modules.workspace.workspace.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataWorkspaceJpaRepository
        extends JpaRepository<WorkspaceJpaEntity, UUID>,
                JpaSpecificationExecutor<WorkspaceJpaEntity> {

    boolean existsByOrganizationIdAndCode(UUID organizationId, String code);
}
