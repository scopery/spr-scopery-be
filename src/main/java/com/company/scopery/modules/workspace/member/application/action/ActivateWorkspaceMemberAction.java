package com.company.scopery.modules.workspace.member.application.action;

import com.company.scopery.modules.workspace.member.application.command.ActivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateWorkspaceMemberAction {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;

    public ActivateWorkspaceMemberAction(WorkspaceMemberRepository workspaceMemberRepository,
                                          WorkspaceRepository workspaceRepository,
                                          WorkspaceActivityLogger activityLogger) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceMemberResponse execute(ActivateWorkspaceMemberCommand command) {
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

    private WorkspaceMember findMemberInWorkspaceOrThrow(UUID memberId, UUID workspaceId) {
        WorkspaceMember member = workspaceMemberRepository.findById(memberId)
                .orElseThrow(() -> WorkspaceExceptions.workspaceMemberNotFound(memberId));
        if (!member.workspaceId().equals(workspaceId)) {
            throw WorkspaceExceptions.workspaceMemberNotFound(memberId);
        }
        return member;
    }
}
