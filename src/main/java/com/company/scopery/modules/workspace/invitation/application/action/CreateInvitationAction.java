package com.company.scopery.modules.workspace.invitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.modules.workspace.invitation.application.command.CreateWorkspaceInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.application.service.WorkspaceInvitationInviteeNotifyService;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreateInvitationAction {

    private static final Logger log = LoggerFactory.getLogger(CreateInvitationAction.class);

    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final NotificationProperties notificationProperties;
    private final IamUserRepository iamUserRepository;
    private final WorkspaceInvitationInviteeNotifyService inviteeNotifyService;
    private final WorkspaceActivityLogger activityLogger;

    public CreateInvitationAction(WorkspaceInvitationRepository invitationRepository,
                                   WorkspaceRepository workspaceRepository,
                                   WorkspaceMemberRepository memberRepository,
                                   CurrentUserAuthorizationService currentUserService,
                                   WorkspaceIamIntegrationService iamIntegrationService,
                                   EmailNotificationTriggerPublisher notificationPublisher,
                                   NotificationProperties notificationProperties,
                                   IamUserRepository iamUserRepository,
                                   WorkspaceInvitationInviteeNotifyService inviteeNotifyService,
                                   WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.notificationPublisher = notificationPublisher;
        this.notificationProperties = notificationProperties;
        this.iamUserRepository = iamUserRepository;
        this.inviteeNotifyService = inviteeNotifyService;
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

        String invitedEmail = command.invitedEmail() == null || command.invitedEmail().isBlank()
                ? null
                : EmailAddress.of(command.invitedEmail()).value();

        Optional<IamUser> inviteeOpt = invitedEmail == null
                ? Optional.empty()
                : iamUserRepository.findByEmail(EmailAddress.of(invitedEmail));

        // Only block when already an active member — revoke + re-invite is allowed.
        inviteeOpt.ifPresent(invitee -> {
            if (memberRepository.isActiveMember(command.workspaceId(), invitee.id())) {
                throw WorkspaceExceptions.workspaceInvitationAlreadyMember(command.workspaceId(), invitee.id());
            }
        });

        String rawCode = InvitationCodeHasher.generateRawCode();

        WorkspaceInvitation invitation = WorkspaceInvitation.create(
                command.workspaceId(), currentUserId, invitedEmail,
                command.maxUses(), command.expiresAt(), rawCode);
        WorkspaceInvitation saved = invitationRepository.save(invitation);

        String acceptUrl = buildAcceptUrl(rawCode);

        if (inviteeOpt.isPresent()) {
            try {
                inviteeNotifyService.notifyInvitee(inviteeOpt.get(), ws, saved, acceptUrl);
            } catch (Exception ex) {
                log.warn("Workspace invitation {}: invitee notify failed for {}: {}",
                        saved.id(), invitedEmail, ex.toString());
            }
        } else if (invitedEmail != null) {
            log.info("Workspace invitation {}: invitee email {} has no IAM user — in-app notification skipped",
                    saved.id(), invitedEmail);
        }

        boolean shouldEmail = Boolean.TRUE.equals(command.sendEmail())
                || (invitedEmail != null && !invitedEmail.isBlank());
        if (shouldEmail && invitedEmail != null) {
            notificationPublisher.publish(new EmailNotificationTriggerPayload(
                    null, "WORKSPACE", "WORKSPACE_INVITATION_CREATED",
                    command.workspaceId(), currentUserId,
                    Map.of("invitee.email", invitedEmail,
                            "workspace.name", ws.name(),
                            "invitation.code", rawCode,
                            "invitation.url", acceptUrl)));
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_INVITATION, saved.id(),
                WorkspaceActivityActions.CREATE_INVITATION, "Invitation created for workspace: " + ws.code().value());

        return WorkspaceInvitationResponse.from(saved, rawCode);
    }

    private String buildAcceptUrl(String rawCode) {
        String base = notificationProperties.getFrontendBaseUrl();
        if (base == null || base.isBlank()) base = "http://localhost:3000";
        return base.replaceAll("/$", "") + "/workspace-invites/" + rawCode;
    }
}
