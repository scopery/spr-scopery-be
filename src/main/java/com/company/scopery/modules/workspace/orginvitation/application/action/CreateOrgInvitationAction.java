package com.company.scopery.modules.workspace.orginvitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
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
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class CreateOrgInvitationAction {

    private final OrgInvitationRepository invitationRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;

    public CreateOrgInvitationAction(OrgInvitationRepository invitationRepository,
                                      OrganizationRepository organizationRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService iamIntegrationService,
                                      WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.organizationRepository = organizationRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgInvitationResponse execute(CreateOrgInvitationCommand command) {
        Organization org = organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(command.organizationId()));

        if (org.status() != OrganizationStatus.ACTIVE) {
            throw WorkspaceExceptions.organizationNotActive(org.code().value());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.ORGANIZATION_MANAGE);

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

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_INVITATION, saved.id(),
                WorkspaceActivityActions.CREATE_ORG_INVITATION,
                "Org invitation created for: " + saved.inviteeEmail());

        return OrgInvitationResponse.from(saved, rawToken);
    }
}
