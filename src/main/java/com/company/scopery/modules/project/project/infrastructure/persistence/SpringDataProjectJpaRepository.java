package com.company.scopery.modules.project.project.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataProjectJpaRepository
        extends JpaRepository<ProjectJpaEntity, UUID>, JpaSpecificationExecutor<ProjectJpaEntity> {

    boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code);
}
