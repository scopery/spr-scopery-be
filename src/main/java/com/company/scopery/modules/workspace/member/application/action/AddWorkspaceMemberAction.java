package com.company.scopery.modules.workspace.member.application.action;

import com.company.scopery.modules.workspace.member.application.command.AddWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddWorkspaceMemberAction {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final WorkspaceActivityLogger activityLogger;

    public AddWorkspaceMemberAction(WorkspaceMemberRepository workspaceMemberRepository,
                                     WorkspaceRepository workspaceRepository,
                                     OrgMemberRepository orgMemberRepository,
                                     WorkspaceActivityLogger activityLogger) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceMemberResponse execute(AddWorkspaceMemberCommand command) {
        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(command.workspaceId(), command.userId())) {
            throw WorkspaceExceptions.workspaceMemberAlreadyExists(command.workspaceId(), command.userId());
        }

        ensureOrgMembership(ws.organizationId(), command.userId());

        WorkspaceMember member = WorkspaceMember.create(command.workspaceId(), command.userId());
        WorkspaceMember saved = workspaceMemberRepository.save(member);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_MEMBER, saved.id(),
                WorkspaceActivityActions.ADD_WORKSPACE_MEMBER,
                "Workspace member added: userId=" + saved.userId() + " to workspaceId=" + saved.workspaceId());

        return WorkspaceMemberResponse.from(saved);
    }

    private void ensureOrgMembership(java.util.UUID organizationId, java.util.UUID userId) {
        if (!orgMemberRepository.isActiveMember(organizationId, userId)) {
            throw WorkspaceExceptions.orgTeamMemberRequiresOrgMember(userId, organizationId);
        }
    }
}
