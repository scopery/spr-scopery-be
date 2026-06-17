package com.company.scopery.modules.workspace.invitation.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.notification.emailtrigger.domain.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.invitation.application.command.CreateWorkspaceInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.domain.InvitationCodeHasher;
import com.company.scopery.modules.workspace.invitation.domain.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkspaceInvitationApplicationService {

    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final WorkspaceActivityLogger activityLogger;

    public WorkspaceInvitationApplicationService(
            WorkspaceInvitationRepository invitationRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository memberRepository,
            CurrentUserAuthorizationService currentUserService,
            WorkspaceIamIntegrationService iamIntegrationService,
            EmailNotificationTriggerPublisher notificationPublisher,
            WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceInvitationResponse createInvitation(CreateWorkspaceInvitationCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(command.workspaceId(), currentUserId, "WORKSPACE_INVITE_MEMBER");

        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));
        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        String rawCode = InvitationCodeHasher.generateRawCode();
        WorkspaceInvitation invitation = WorkspaceInvitation.create(
                command.workspaceId(), currentUserId, command.invitedEmail(),
                command.maxUses(), command.expiresAt(), rawCode);
        WorkspaceInvitation saved = invitationRepository.save(invitation);

        if (command.sendEmail() && command.invitedEmail() != null && !command.invitedEmail().isBlank()) {
            notificationPublisher.publish(new EmailNotificationTriggerPayload(
                    null, "WORKSPACE", "WORKSPACE_INVITATION_CREATED",
                    command.workspaceId(), currentUserId,
                    Map.of("invitee.email", command.invitedEmail(),
                            "workspace.name", ws.name(),
                            "invitation.code", rawCode)));
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_INVITATION, saved.id(),
                WorkspaceActivityActions.CREATE_INVITATION, "Invitation created for workspace: " + ws.code().value());

        return WorkspaceInvitationResponse.from(saved, rawCode);
    }

    @Transactional
    public WorkspaceInvitationResponse acceptInvitation(String rawCode) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        String codeHash = InvitationCodeHasher.hash(rawCode);

        WorkspaceInvitation invitation = invitationRepository.findByCodeHash(codeHash)
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_NOT_FOUND,
                        "Invitation not found", null));

        if (memberRepository.isActiveMember(invitation.workspaceId(), currentUserId)) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_ALREADY_MEMBER,
                    "You are already a member of this workspace", null);
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

    @Transactional
    public WorkspaceInvitationResponse revokeInvitation(UUID id, UUID workspaceId) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(workspaceId, currentUserId, "WORKSPACE_MANAGE_INVITATION");

        WorkspaceInvitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_NOT_FOUND,
                        "Invitation not found: " + id, null));

        WorkspaceInvitation revoked = invitation.revoke();
        WorkspaceInvitation saved = invitationRepository.save(revoked);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_INVITATION, saved.id(),
                WorkspaceActivityActions.REVOKE_INVITATION, "Invitation revoked");

        return WorkspaceInvitationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceInvitationResponse> listInvitations(UUID workspaceId) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(workspaceId, currentUserId, "WORKSPACE_MANAGE_INVITATION");
        return invitationRepository.findByWorkspaceId(workspaceId).stream()
                .map(WorkspaceInvitationResponse::from)
                .toList();
    }
}
