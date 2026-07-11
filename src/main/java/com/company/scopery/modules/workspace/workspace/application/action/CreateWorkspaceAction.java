package com.company.scopery.modules.workspace.workspace.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Map;

@Component
public class CreateWorkspaceAction {

    private final WorkspaceRepository workspaceRepository;
    private final OrganizationRepository organizationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ImmutableAuditEventService auditEventService;
    private final TransactionalOutboxService outboxService;

    public CreateWorkspaceAction(WorkspaceRepository workspaceRepository,
                                  OrganizationRepository organizationRepository,
                                  WorkspaceMemberRepository workspaceMemberRepository,
                                  WorkspaceActivityLogger activityLogger,
                                  CurrentUserAuthorizationService currentUserService,
                                  WorkspaceIamIntegrationService iamIntegrationService,
                                  ImmutableAuditEventService auditEventService,
                                  TransactionalOutboxService outboxService) {
        this.workspaceRepository = workspaceRepository;
        this.organizationRepository = organizationRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.auditEventService = auditEventService;
        this.outboxService = outboxService;
    }

    @Transactional
    public WorkspaceDetailResponse execute(CreateWorkspaceCommand command) {
        UUID ownerUserId = currentUserService.resolveCurrentUser().id();

        Organization org = organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(command.organizationId()));

        if (org.status() != OrganizationStatus.ACTIVE) {
            throw WorkspaceExceptions.organizationNotActive(org.code().value());
        }

        iamIntegrationService.requireOrgAccess(
                command.organizationId(), ownerUserId, IamAuthorities.ORGANIZATION_CREATE_WORKSPACE);

        WorkspaceCode code = WorkspaceCode.of(command.code());

        if (workspaceRepository.existsByOrganizationIdAndCode(command.organizationId(), code)) {
            throw WorkspaceExceptions.workspaceCodeAlreadyExists(code.value());
        }

        WorkspaceVisibility visibility = WorkspaceEnumParser.parseOptional(
                WorkspaceVisibility.class, command.defaultVisibility(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_VISIBILITY.code(), "defaultVisibility");
        if (visibility == null) {
            visibility = WorkspaceVisibility.PRIVATE;
        }

        WorkspaceJoinPolicy joinPolicy = WorkspaceEnumParser.parseOptional(
                WorkspaceJoinPolicy.class, command.joinPolicy(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_JOIN_POLICY.code(), "joinPolicy");
        if (joinPolicy == null) {
            joinPolicy = WorkspaceJoinPolicy.INVITE_ONLY;
        }

        Workspace ws = Workspace.create(command.organizationId(), command.name(), code,
                command.description(), ownerUserId, visibility, joinPolicy);
        Workspace saved = workspaceRepository.save(ws);

        try {
            WorkspaceMember owner = WorkspaceMember.create(saved.id(), ownerUserId);
            workspaceMemberRepository.save(owner);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceOwnerMemberBootstrapFailed(saved.id());
        }

        try {
            iamIntegrationService.bootstrapWorkspaceAccess(
                    saved.id(), saved.organizationId(), saved.name(), ownerUserId);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceIamBootstrapFailed("WORKSPACE", saved.id(), e.getMessage());
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.CREATE_WORKSPACE,
                "Workspace created: " + saved.code().value());

        Map<String, Object> event = Map.of("id", saved.id(), "organizationId", saved.organizationId(),
                "code", saved.code().value(), "name", saved.name());
        outboxService.enqueue("WORKSPACE", saved.id(), "WORKSPACE_CREATED", event);
        auditEventService.record(AuditEventType.WORKSPACE_CREATED, ownerUserId, "USER",
                "WORKSPACE", saved.id(), saved.organizationId(), saved.id(), null, event, "Workspace created");

        return WorkspaceDetailResponse.from(saved, true);
    }
}
