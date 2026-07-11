package com.company.scopery.modules.workspace.member.application.action;

import com.company.scopery.modules.workspace.member.application.command.DeactivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeactivateWorkspaceMemberAction {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceActivityLogger activityLogger;

    public DeactivateWorkspaceMemberAction(WorkspaceMemberRepository workspaceMemberRepository,
                                            WorkspaceActivityLogger activityLogger) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceMemberResponse execute(DeactivateWorkspaceMemberCommand command) {
        WorkspaceMember member = findMemberInWorkspaceOrThrow(command.memberId(), command.workspaceId());
        WorkspaceMember deactivated = member.deactivate();
        WorkspaceMember saved = workspaceMemberRepository.save(deactivated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_MEMBER, saved.id(),
                WorkspaceActivityActions.DEACTIVATE_WORKSPACE_MEMBER,
                "Workspace member deactivated: memberId=" + saved.id());

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
