package com.company.scopery.modules.workspace.member.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.member.application.query.SearchWorkspaceMemberQuery;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceMemberQueryService {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceMemberQueryService(WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Transactional(readOnly = true)
    public PageResult<WorkspaceMemberResponse> searchMembers(SearchWorkspaceMemberQuery query) {
        WorkspaceMemberStatus status = WorkspaceEnumParser.parseOptional(
                WorkspaceMemberStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_MEMBER_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), WorkspaceSortFields.CREATED_AT, false);
        return workspaceMemberRepository.findAll(query.workspaceId(), query.userId(), status, pageQuery)
                .map(WorkspaceMemberResponse::from);
    }
}
