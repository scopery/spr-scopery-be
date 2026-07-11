package com.company.scopery.modules.workspace.workspace.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import com.company.scopery.modules.workspace.workspace.application.query.SearchWorkspaceQuery;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkspaceQueryService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceQueryService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional(readOnly = true)
    public WorkspaceDetailResponse getWorkspace(UUID id) {
        return WorkspaceDetailResponse.from(findOrThrow(id), false);
    }

    @Transactional(readOnly = true)
    public PageResult<WorkspaceResponse> searchWorkspaces(SearchWorkspaceQuery query) {
        WorkspaceStatus status = WorkspaceEnumParser.parseOptional(
                WorkspaceStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), WorkspaceSortFields.CREATED_AT, false);
        return workspaceRepository.findAll(query.organizationId(), query.ownerUserId(), query.keyword(), status,
                pageQuery).map(WorkspaceResponse::from);
    }

    private Workspace findOrThrow(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(id));
    }
}
