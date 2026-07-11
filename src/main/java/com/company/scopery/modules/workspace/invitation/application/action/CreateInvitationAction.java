package com.company.scopery.modules.workspace.invitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.invitation.application.command.CreateWorkspaceInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class CreateInvitationAction {

    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final WorkspaceActivityLogger activityLogger;

    public CreateInvitationAction(WorkspaceInvitationRepository invitationRepository,
                                   WorkspaceRepository workspaceRepository,
                                   CurrentUserAuthorizationService currentUserService,
                                   WorkspaceIamIntegrationService iamIntegrationService,
                                   EmailNotificationTriggerPublisher notificationPublisher,
                                   WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.workspaceRepository = workspaceRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceInvitationResponse execute(CreateWorkspaceInvitationCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(
                command.workspaceId(), currentUserId, IamAuthorities.WORKSPACE_INVITE_MEMBER);

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
}
