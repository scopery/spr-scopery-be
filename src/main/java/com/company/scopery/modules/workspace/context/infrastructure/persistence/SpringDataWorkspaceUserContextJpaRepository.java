package com.company.scopery.modules.workspace.context.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataWorkspaceUserContextJpaRepository
        extends JpaRepository<WorkspaceUserContextJpaEntity, UUID> {}
