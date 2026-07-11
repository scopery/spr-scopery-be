package com.company.scopery.modules.workspace.orgmember.application.action;

import com.company.scopery.modules.workspace.orgmember.application.command.AddOrgMemberCommand;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMemberResponse;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;

import java.util.UUID;

@Component
public class AddOrgMemberAction {

    private final OrgMemberRepository orgMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ImmutableAuditEventService auditEventService;

    public AddOrgMemberAction(OrgMemberRepository orgMemberRepository,
                               OrganizationRepository organizationRepository,
                               WorkspaceActivityLogger activityLogger,
                               CurrentUserAuthorizationService currentUserService,
                               WorkspaceIamIntegrationService iamIntegrationService,
                               ImmutableAuditEventService auditEventService) {
        this.orgMemberRepository = orgMemberRepository;
        this.organizationRepository = organizationRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public OrgMemberResponse execute(AddOrgMemberCommand command) {
        Organization org = organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(command.organizationId()));

        if (org.status() != OrganizationStatus.ACTIVE) {
            throw WorkspaceExceptions.organizationNotActive(org.code().value());
        }

        UUID actorId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.ORGANIZATION_MANAGE);

        if (orgMemberRepository.existsByOrganizationIdAndUserId(command.organizationId(), command.userId())) {
            throw WorkspaceExceptions.orgMemberAlreadyExists(command.organizationId(), command.userId());
        }

        OrgMembershipType membershipType = WorkspaceEnumParser.parseRequired(
                OrgMembershipType.class, command.membershipType(),
                WorkspaceErrorCatalog.INVALID_ORG_MEMBERSHIP_TYPE.code(), "membershipType");

        OrgMember member = OrgMember.create(command.organizationId(), command.userId(), membershipType,
                OrgMembershipSource.MANUAL);
        OrgMember saved = orgMemberRepository.save(member);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_MEMBER, saved.id(),
                WorkspaceActivityActions.ADD_ORG_MEMBER,
                "Org member added: userId=" + saved.userId() + " to orgId=" + saved.organizationId());
        auditEventService.record(AuditEventType.ORG_MEMBER_ENROLLED,
                actorId, "USER", "ORG_MEMBER", saved.id(),
                saved.organizationId(), null, null, OrgMemberResponse.from(saved), "Member enrolled");

        return OrgMemberResponse.from(saved);
    }
}
