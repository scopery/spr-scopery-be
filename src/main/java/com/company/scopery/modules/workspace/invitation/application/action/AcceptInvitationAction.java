package com.company.scopery.modules.workspace.invitation.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.invitation.application.command.AcceptInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class AcceptInvitationAction {

    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final WorkspaceActivityLogger activityLogger;

    public AcceptInvitationAction(WorkspaceInvitationRepository invitationRepository,
                                   WorkspaceMemberRepository memberRepository,
                                   OrgMemberRepository orgMemberRepository,
                                   WorkspaceRepository workspaceRepository,
                                   CurrentUserAuthorizationService currentUserService,
                                   EmailNotificationTriggerPublisher notificationPublisher,
                                   WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.memberRepository = memberRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.currentUserService = currentUserService;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceInvitationResponse execute(AcceptInvitationCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        String codeHash = InvitationCodeHasher.hash(command.rawCode());

        WorkspaceInvitation invitation = invitationRepository.findByCodeHash(codeHash)
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_NOT_FOUND,
                        "Invitation not found", null));

        if (memberRepository.isActiveMember(invitation.workspaceId(), currentUserId)) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_ALREADY_MEMBER,
                    "You are already a member of this workspace", null);
        }

        Workspace ws = workspaceRepository.findById(invitation.workspaceId()).orElse(null);
        if (ws != null && !orgMemberRepository.existsByOrganizationIdAndUserId(ws.organizationId(), currentUserId)) {
            orgMemberRepository.save(OrgMember.create(ws.organizationId(), currentUserId, OrgMembershipType.MEMBER,
                    OrgMembershipSource.WORKSPACE_INVITATION));
        } else if (ws != null && !orgMemberRepository.isActiveMember(ws.organizationId(), currentUserId)) {
            throw WorkspaceExceptions.orgTeamMemberRequiresOrgMember(currentUserId, ws.organizationId());
        }

        WorkspaceInvitation updated = invitation.accept();
        invitationRepository.save(updated);

        memberRepository.save(WorkspaceMember.create(invitation.workspaceId(), currentUserId));

        notificationPublisher.publish(new EmailNotificationTriggerPayload(
                null, "WORKSPACE", "WORKSPACE_INVITATION_ACCEPTED",
                invitation.workspaceId(), currentUserId, Map.of()));

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_INVITATION, updated.id(),
                WorkspaceActivityActions.ACCEPT_INVITATION, "Invitation accepted");

        return WorkspaceInvitationResponse.from(updated);
    }
}
