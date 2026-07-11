package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.modules.workspace.orgteam.application.command.CreateOrgTeamCommand;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamResponse;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.orgteam.domain.valueobject.OrgTeamCode;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateOrgTeamAction {

    private final OrgTeamRepository orgTeamRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final OrganizationRepository organizationRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ImmutableAuditEventService auditEventService;
    private final TransactionalOutboxService outboxService;

    public CreateOrgTeamAction(OrgTeamRepository orgTeamRepository,
                                WorkspaceActivityLogger activityLogger,
                                OrganizationRepository organizationRepository,
                                CurrentUserAuthorizationService currentUserService,
                                WorkspaceIamIntegrationService iamIntegrationService,
                                ImmutableAuditEventService auditEventService,
                                TransactionalOutboxService outboxService) {
        this.orgTeamRepository = orgTeamRepository;
        this.activityLogger = activityLogger;
        this.organizationRepository = organizationRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.auditEventService = auditEventService;
        this.outboxService = outboxService;
    }

    @Transactional
    public OrgTeamResponse execute(CreateOrgTeamCommand command) {
        Organization organization = organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(command.organizationId()));
        if (organization.status() != OrganizationStatus.ACTIVE) {
            throw WorkspaceExceptions.organizationNotActive(organization.code().value());
        }

        var actor = currentUserService.resolveCurrentUser();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actor.id(), IamAuthorities.TEAM_CREATE);

        OrgTeamCode code = OrgTeamCode.of(command.code());

        if (orgTeamRepository.existsByOrganizationIdAndCode(command.organizationId(), code)) {
            throw WorkspaceExceptions.orgTeamCodeAlreadyExists(code.value(), command.organizationId());
        }

        OrgTeam team = OrgTeam.create(command.organizationId(), command.name(), code, command.description());
        OrgTeam saved = orgTeamRepository.save(team);

        iamIntegrationService.bootstrapOrganizationTeamAccess(
                saved.id(), saved.organizationId(), saved.name(), actor.id());

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_TEAM, saved.id(),
                WorkspaceActivityActions.CREATE_ORG_TEAM,
                "Org team created: " + saved.code().value());

        var event = java.util.Map.of("id", saved.id(), "organizationId", saved.organizationId(),
                "code", saved.code().value(), "name", saved.name());
        outboxService.enqueue("ORG_TEAM", saved.id(), "ORG_TEAM_CREATED", event);
        auditEventService.record(AuditEventType.ORG_TEAM_CREATED, actor.id(), "USER",
                "ORG_TEAM", saved.id(), saved.organizationId(), null, null, event, "Organization team created");

        return OrgTeamResponse.from(saved);
    }
}
