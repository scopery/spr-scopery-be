package com.company.scopery.modules.workspace.member.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.workspace.member.application.command.ActivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.command.AddWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.command.DeactivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.query.SearchWorkspaceMemberQuery;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkspaceMemberApplicationService {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;

    public WorkspaceMemberApplicationService(WorkspaceMemberRepository workspaceMemberRepository,
                                              WorkspaceRepository workspaceRepository,
                                              WorkspaceActivityLogger activityLogger) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceMemberResponse addMember(AddWorkspaceMemberCommand command) {
        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(command.workspaceId(), command.userId())) {
            throw WorkspaceExceptions.workspaceMemberAlreadyExists(command.workspaceId(), command.userId());
        }

        WorkspaceMember member = WorkspaceMember.create(command.workspaceId(), command.userId());
        WorkspaceMember saved = workspaceMemberRepository.save(member);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_MEMBER, saved.id(),
                WorkspaceActivityActions.ADD_WORKSPACE_MEMBER,
                "Workspace member added: userId=" + saved.userId() + " to workspaceId=" + saved.workspaceId());

        return WorkspaceMemberResponse.from(saved);
    }

    @Transactional
    public WorkspaceMemberResponse activateMember(ActivateWorkspaceMemberCommand command) {
        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));
        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceMemberCannotBeActivatedForInactiveWorkspace(ws.id());
        }
        WorkspaceMember member = findMemberInWorkspaceOrThrow(command.memberId(), command.workspaceId());
        WorkspaceMember activated = member.activate();
        WorkspaceMember saved = workspaceMemberRepository.save(activated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_MEMBER, saved.id(),
                WorkspaceActivityActions.ACTIVATE_WORKSPACE_MEMBER,
                "Workspace member activated: memberId=" + saved.id());

        return WorkspaceMemberResponse.from(saved);
    }

    @Transactional
    public WorkspaceMemberResponse deactivateMember(DeactivateWorkspaceMemberCommand command) {
        WorkspaceMember member = findMemberInWorkspaceOrThrow(command.memberId(), command.workspaceId());
        WorkspaceMember deactivated = member.deactivate();
        WorkspaceMember saved = workspaceMemberRepository.save(deactivated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_MEMBER, saved.id(),
                WorkspaceActivityActions.DEACTIVATE_WORKSPACE_MEMBER,
                "Workspace member deactivated: memberId=" + saved.id());

        return WorkspaceMemberResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<WorkspaceMemberResponse> searchMembers(SearchWorkspaceMemberQuery query) {
        WorkspaceMemberStatus status = WorkspaceEnumParser.parseOptional(
                WorkspaceMemberStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_MEMBER_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, WorkspaceSortFields.CREATED_AT));
        return workspaceMemberRepository.findAll(query.workspaceId(), query.userId(), status, pageable)
                .map(WorkspaceMemberResponse::from);
    }

    private WorkspaceMember findMemberInWorkspaceOrThrow(UUID memberId, UUID workspaceId) {
        WorkspaceMember member = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> WorkspaceExceptions.workspaceMemberNotFound(memberId));
        if (!member.workspaceId().equals(workspaceId)) {
            throw WorkspaceExceptions.workspaceMemberNotFound(memberId);
        }
        return member;
    }
}
