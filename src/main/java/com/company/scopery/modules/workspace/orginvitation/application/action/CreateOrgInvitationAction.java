package com.company.scopery.modules.workspace.orginvitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.orginvitation.application.command.CreateOrgInvitationCommand;
import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.WorkspaceAudienceResolver;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreateOrgInvitationAction {

    private static final Logger log = LoggerFactory.getLogger(CreateOrgInvitationAction.class);

    private final OrgInvitationRepository invitationRepository;
    private final OrganizationRepository organizationRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;
    private final IamUserRepository iamUserRepository;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final NotificationProperties notificationProperties;
    private final InAppDeliveryService inAppDeliveryService;
    private final WorkspaceAudienceResolver audienceResolver;

    public CreateOrgInvitationAction(OrgInvitationRepository invitationRepository,
                                      OrganizationRepository organizationRepository,
                                      OrgMemberRepository orgMemberRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService iamIntegrationService,
                                      WorkspaceActivityLogger activityLogger,
                                      IamUserRepository iamUserRepository,
                                      EmailNotificationTriggerPublisher notificationPublisher,
                                      NotificationProperties notificationProperties,
                                      InAppDeliveryService inAppDeliveryService,
                                      WorkspaceAudienceResolver audienceResolver) {
        this.invitationRepository = invitationRepository;
        this.organizationRepository = organizationRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
        this.iamUserRepository = iamUserRepository;
        this.notificationPublisher = notificationPublisher;
        this.notificationProperties = notificationProperties;
        this.inAppDeliveryService = inAppDeliveryService;
        this.audienceResolver = audienceResolver;
    }

    @Transactional
    public OrgInvitationResponse execute(CreateOrgInvitationCommand command) {
        Organization org = organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(command.organizationId()));

        if (org.status() != OrganizationStatus.ACTIVE) {
            throw WorkspaceExceptions.organizationNotActive(org.code().value());
        }

        IamUser actor = currentUserAuthorizationService.resolveCurrentUser();
        UUID actorId = actor.id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.ORGANIZATION_MANAGE);

        Optional<IamUser> inviteeOpt = iamUserRepository.findByEmail(EmailAddress.of(command.inviteeEmail()));
        inviteeOpt.ifPresent(invitee -> {
            if (orgMemberRepository.isActiveMember(command.organizationId(), invitee.id())) {
                throw WorkspaceExceptions.orgInvitationAlreadyMember(command.organizationId(), invitee.id());
            }
        });

        OrgMembershipType membershipType = WorkspaceEnumParser.parseOptional(
                OrgMembershipType.class, command.membershipType(),
                WorkspaceErrorCatalog.INVALID_ORG_MEMBERSHIP_TYPE.code(), "membershipType");
        if (membershipType == null) membershipType = OrgMembershipType.MEMBER;

        Instant expiresAt = command.expiresAt() != null
                ? command.expiresAt()
                : Instant.now().plus(7, ChronoUnit.DAYS);

        String rawToken = InvitationCodeHasher.generateRawCode();
        OrgInvitation invitation = OrgInvitation.create(
                command.organizationId(), command.inviteeEmail(), membershipType,
                actorId, rawToken, expiresAt);

        OrgInvitation saved = invitationRepository.save(invitation);

        String acceptUrl = buildAcceptUrl(rawToken);

        // Work Inbox only for invitee (no Accept CTA on Notification)
        inviteeOpt.ifPresent(invitee -> {
            try {
                inAppDeliveryService.deliverWorkInbox(
                        null,
                        audienceResolver.explicitUser(invitee.id()),
                        InAppDeliveryService.SOURCE_ORG_INVITATION,
                        saved.id(),
                        InAppDeliveryService.ACTION_ACCEPT_ORG,
                        "Invite to join " + org.name(),
                        "HIGH",
                        saved.expiresAt());
            } catch (Exception ex) {
                log.warn("Org invitation {}: work-inbox failed for {}: {}",
                        saved.id(), command.inviteeEmail(), ex.toString());
            }
        });

        publishEmailTrigger(actor, org, command.inviteeEmail(), acceptUrl, saved.expiresAt(), inviteeOpt);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_INVITATION, saved.id(),
                WorkspaceActivityActions.CREATE_ORG_INVITATION,
                "Org invitation created for: " + saved.inviteeEmail());

        return OrgInvitationResponse.from(saved, rawToken);
    }

    private String buildAcceptUrl(String rawToken) {
        String base = notificationProperties.getFrontendBaseUrl();
        if (base == null || base.isBlank()) base = "http://localhost:3000";
        return base.replaceAll("/$", "") + "/org-invites/" + rawToken;
    }

    private void publishEmailTrigger(IamUser actor,
                                     Organization org,
                                     String inviteeEmail,
                                     String acceptUrl,
                                     Instant expiresAt,
                                     Optional<IamUser> inviteeOpt) {
        Map<String, Object> invitee = new LinkedHashMap<>();
        invitee.put("email", inviteeEmail);
        inviteeOpt.ifPresent(u -> {
            invitee.put("userId", u.id().toString());
            if (u.fullName() != null) invitee.put("name", u.fullName());
        });

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("invitee", invitee);
        payload.put("organization", Map.of(
                "id", org.id().toString(),
                "name", org.name()));
        payload.put("invitation", Map.of(
                "url", acceptUrl,
                "expiresAt", expiresAt.toString()));
        payload.put("inviter", Map.of(
                "name", actor.fullName() != null ? actor.fullName() : actor.email().value()));

        notificationPublisher.publish(new EmailNotificationTriggerPayload(
                null,
                "SCOPERY_WORKSPACE",
                "ORG_INVITATION_CREATED",
                null,
                actor.id(),
                payload));
    }
}
