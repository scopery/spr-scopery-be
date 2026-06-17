package com.company.scopery.modules.workspace.workspace.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.organization.domain.Organization;
import com.company.scopery.modules.workspace.organization.domain.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.command.UpdateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.query.SearchWorkspaceQuery;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkspaceApplicationService {

    private final WorkspaceRepository workspaceRepository;
    private final OrganizationRepository organizationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public WorkspaceApplicationService(WorkspaceRepository workspaceRepository,
                                        OrganizationRepository organizationRepository,
                                        WorkspaceMemberRepository workspaceMemberRepository,
                                        WorkspaceActivityLogger activityLogger,
                                        CurrentUserAuthorizationService currentUserService,
                                        WorkspaceIamIntegrationService iamIntegrationService) {
        this.workspaceRepository = workspaceRepository;
        this.organizationRepository = organizationRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
    }

    @Transactional
    public WorkspaceDetailResponse createWorkspace(CreateWorkspaceCommand command) {
        UUID ownerUserId = currentUserService.resolveCurrentUser().id();

        Organization org = organizationRepository.findById(command.organizationId())
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(command.organizationId()));

        if (org.status() != OrganizationStatus.ACTIVE) {
            throw WorkspaceExceptions.organizationNotActive(org.code().value());
        }

        // Check that caller has CREATE_WORKSPACE right on the organization's IAM resource
        iamIntegrationService.requireOrgAccess(command.organizationId(), ownerUserId, "CREATE_WORKSPACE");

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

        // Bootstrap owner membership in the same transaction
        try {
            WorkspaceMember owner = WorkspaceMember.create(saved.id(), ownerUserId);
            workspaceMemberRepository.save(owner);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceOwnerMemberBootstrapFailed(saved.id());
        }

        try {
            iamIntegrationService.bootstrapWorkspaceAccess(saved.id(), saved.name(), ownerUserId);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceIamBootstrapFailed("WORKSPACE", saved.id());
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.CREATE_WORKSPACE,
                "Workspace created: " + saved.code().value());

        return WorkspaceDetailResponse.from(saved, true);
    }

    @Transactional
    public WorkspaceResponse updateWorkspace(UpdateWorkspaceCommand command) {
        Workspace ws = findOrThrow(command.id());
        if (ws.status() == WorkspaceStatus.ARCHIVED) {
            throw WorkspaceExceptions.workspaceArchivedCannotBeUpdated(ws.code().value());
        }

        WorkspaceVisibility visibility = WorkspaceEnumParser.parseOptional(
                WorkspaceVisibility.class, command.defaultVisibility(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_VISIBILITY.code(), "defaultVisibility");
        if (visibility == null) {
            visibility = ws.defaultVisibility();
        }

        WorkspaceJoinPolicy joinPolicy = WorkspaceEnumParser.parseOptional(
                WorkspaceJoinPolicy.class, command.joinPolicy(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_JOIN_POLICY.code(), "joinPolicy");
        if (joinPolicy == null) {
            joinPolicy = ws.joinPolicy();
        }

        Workspace updated = ws.update(command.name(), command.description(), visibility, joinPolicy);
        Workspace saved = workspaceRepository.save(updated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.UPDATE_WORKSPACE,
                "Workspace updated: " + saved.code().value());

        return WorkspaceResponse.from(saved);
    }

    @Transactional
    public WorkspaceResponse activateWorkspace(UUID id) {
        Workspace ws = findOrThrow(id);

        if (ws.status() == WorkspaceStatus.ARCHIVED) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        Workspace activated = ws.activate();
        Workspace saved = workspaceRepository.save(activated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.ACTIVATE_WORKSPACE,
                "Workspace activated: " + saved.code().value());

        return WorkspaceResponse.from(saved);
    }

    @Transactional
    public WorkspaceResponse archiveWorkspace(UUID id) {
        Workspace ws = findOrThrow(id);
        Workspace archived = ws.archive();
        Workspace saved = workspaceRepository.save(archived);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.ARCHIVE_WORKSPACE,
                "Workspace archived: " + saved.code().value());

        return WorkspaceResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public WorkspaceDetailResponse getWorkspace(UUID id) {
        return WorkspaceDetailResponse.from(findOrThrow(id), false);
    }

    @Transactional(readOnly = true)
    public Page<WorkspaceResponse> searchWorkspaces(SearchWorkspaceQuery query) {
        WorkspaceStatus status = WorkspaceEnumParser.parseOptional(
                WorkspaceStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, WorkspaceSortFields.CREATED_AT));
        return workspaceRepository.findAll(query.organizationId(), query.ownerUserId(), query.keyword(), status,
                pageable).map(WorkspaceResponse::from);
    }

    private Workspace findOrThrow(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(id));
    }
}
