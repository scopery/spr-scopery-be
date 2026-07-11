package com.company.scopery.modules.workspace.workspace.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    Optional<Workspace> findById(UUID id);

    Optional<Workspace> findByCode(WorkspaceCode code);

    boolean existsByOrganizationIdAndCode(UUID organizationId, WorkspaceCode code);

    List<Workspace> findActiveByMemberId(UUID userId);
    List<Workspace> findAllActiveByOrganizationId(UUID organizationId);

    PageResult<Workspace> findAll(UUID organizationId, UUID ownerUserId, String keyword, WorkspaceStatus status, PageQuery pageQuery);
}
