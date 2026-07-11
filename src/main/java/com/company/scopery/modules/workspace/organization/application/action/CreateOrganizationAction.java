package com.company.scopery.modules.workspace.organization.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.workspace.organization.application.command.CreateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.valueobject.OrganizationCode;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Map;

@Component
public class CreateOrganizationAction {

    private final OrganizationRepository organizationRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final OrgMemberRepository orgMemberRepository;
    private final ImmutableAuditEventService auditEventService;
    private final TransactionalOutboxService outboxService;

    public CreateOrganizationAction(OrganizationRepository organizationRepository,
                                     WorkspaceActivityLogger activityLogger,
                                     CurrentUserAuthorizationService currentUserService,
                                     WorkspaceIamIntegrationService iamIntegrationService,
                                     OrgMemberRepository orgMemberRepository,
                                     ImmutableAuditEventService auditEventService,
                                     TransactionalOutboxService outboxService) {
        this.organizationRepository = organizationRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.orgMemberRepository = orgMemberRepository;
        this.auditEventService = auditEventService;
        this.outboxService = outboxService;
    }

    @Transactional
    public OrganizationResponse execute(CreateOrganizationCommand command) {
        UUID ownerUserId = currentUserService.resolveCurrentUser().id();
        OrganizationCode code = OrganizationCode.of(command.code());

        if (organizationRepository.existsByCode(code)) {
            throw WorkspaceExceptions.organizationCodeAlreadyExists(code.value());
        }

        Organization org = Organization.create(command.name(), code, command.description(), ownerUserId);
        Organization saved = organizationRepository.save(org);

        orgMemberRepository.save(OrgMember.create(saved.id(), ownerUserId, OrgMembershipType.OWNER,
                OrgMembershipSource.SYSTEM_BOOTSTRAP));

        try {
            iamIntegrationService.bootstrapOrganizationAccess(saved.id(), saved.name(), ownerUserId);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceIamBootstrapFailed("ORGANIZATION", saved.id(), e.getMessage());
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.CREATE_ORGANIZATION,
                "Organization created: " + saved.code().value());

        Map<String, Object> event = Map.of("id", saved.id(), "code", saved.code().value(), "name", saved.name());
        outboxService.enqueue("ORGANIZATION", saved.id(), "ORGANIZATION_CREATED", event);
        auditEventService.record(AuditEventType.ORGANIZATION_CREATED, ownerUserId, "USER",
                "ORGANIZATION", saved.id(), saved.id(), null, null, event, "Organization created");

        return OrganizationResponse.from(saved);
    }
}
