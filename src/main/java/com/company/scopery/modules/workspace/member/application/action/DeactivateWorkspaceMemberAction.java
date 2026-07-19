package com.company.scopery.modules.workspace.member.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.member.application.command.DeactivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.application.service.WorkspaceMembershipAccessRevocationService;
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
    private final WorkspaceMembershipAccessRevocationService revocationService;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public DeactivateWorkspaceMemberAction(WorkspaceMemberRepository workspaceMemberRepository,
                                            WorkspaceMembershipAccessRevocationService revocationService,
                                            CurrentUserAuthorizationService currentUserService,
                                            WorkspaceActivityLogger activityLogger,
                                            ImmutableAuditEventService auditEventService) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.revocationService = revocationService;
        this.currentUserService = currentUserService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public WorkspaceMemberResponse execute(DeactivateWorkspaceMemberCommand command) {
        WorkspaceMember member = findMemberInWorkspaceOrThrow(command.memberId(), command.workspaceId());
        WorkspaceMember deactivated = member.deactivate();
        WorkspaceMember saved = workspaceMemberRepository.save(deactivated);

        UUID actorId = currentUserService.resolveCurrentUser().id();
        revocationService.revokeWorkspaceScopedAccess(command.workspaceId(), saved.userId(), actorId);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_MEMBER, saved.id(),
                WorkspaceActivityActions.DEACTIVATE_WORKSPACE_MEMBER,
                "Workspace member deactivated: memberId=" + saved.id());

        auditEventService.record(AuditEventType.WORKSPACE_MEMBER_DEACTIVATED, actorId, "USER",
                "WORKSPACE_MEMBER", saved.id(), null, command.workspaceId(), null,
                java.util.Map.of("userId", saved.userId().toString(), "status", saved.status().name()),
                "Workspace member deactivated");

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
