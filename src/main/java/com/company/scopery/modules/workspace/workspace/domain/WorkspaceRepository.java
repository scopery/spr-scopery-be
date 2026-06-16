package com.company.scopery.modules.workspace.workspace.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    Optional<Workspace> findById(UUID id);

    boolean existsByOrganizationIdAndCode(UUID organizationId, WorkspaceCode code);

    Page<Workspace> findAll(UUID organizationId, UUID ownerUserId, String keyword, WorkspaceStatus status, Pageable pageable);
}
