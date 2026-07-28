package com.company.scopery.modules.iam.grant.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.application.action.BootstrapOrganizationAccessAction;
import com.company.scopery.modules.iam.grant.application.action.BootstrapOrganizationTeamAccessAction;
import com.company.scopery.modules.iam.grant.application.action.BootstrapProjectAccessAction;
import com.company.scopery.modules.iam.grant.application.action.BootstrapTeamAccessAction;
import com.company.scopery.modules.iam.grant.application.action.BootstrapWorkspaceAccessAction;
import com.company.scopery.modules.iam.grant.application.action.EnsureProjectMemberBaselineAccessAction;
import com.company.scopery.modules.iam.grant.application.action.EnsureWorkspaceMemberBaselineAccessAction;
import com.company.scopery.modules.iam.grant.application.command.BootstrapOrganizationAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.BootstrapOrganizationTeamAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.BootstrapProjectAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.BootstrapTeamAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.BootstrapWorkspaceAccessCommand;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.workspace.member.application.service.MemberProjectAccessInferenceService;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Facade for workspace module integration: bootstraps IAM resources/grants and enforces access guards.
 */
@Service
public class WorkspaceIamIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceIamIntegrationService.class);

    private final IamAuthResourceRepository authResourceRepository;
    private final AuthorizationDecisionService decisionService;
    private final BootstrapOrganizationAccessAction bootstrapOrganizationAccessAction;
    private final BootstrapWorkspaceAccessAction bootstrapWorkspaceAccessAction;
    private final BootstrapProjectAccessAction bootstrapProjectAccessAction;
    private final BootstrapTeamAccessAction bootstrapTeamAccessAction;
    private final BootstrapOrganizationTeamAccessAction bootstrapOrganizationTeamAccessAction;
    private final EnsureWorkspaceMemberBaselineAccessAction ensureWorkspaceMemberBaselineAccessAction;
    private final EnsureProjectMemberBaselineAccessAction ensureProjectMemberBaselineAccessAction;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final MemberProjectAccessInferenceService memberProjectAccessInferenceService;

    public WorkspaceIamIntegrationService(
            IamAuthResourceRepository authResourceRepository,
            AuthorizationDecisionService decisionService,
            BootstrapOrganizationAccessAction bootstrapOrganizationAccessAction,
            BootstrapWorkspaceAccessAction bootstrapWorkspaceAccessAction,
            BootstrapProjectAccessAction bootstrapProjectAccessAction,
            BootstrapTeamAccessAction bootstrapTeamAccessAction,
            BootstrapOrganizationTeamAccessAction bootstrapOrganizationTeamAccessAction,
            EnsureWorkspaceMemberBaselineAccessAction ensureWorkspaceMemberBaselineAccessAction,
            EnsureProjectMemberBaselineAccessAction ensureProjectMemberBaselineAccessAction,
            WorkspaceMemberRepository workspaceMemberRepository,
            ProjectRepository projectRepository,
            MemberProjectAccessInferenceService memberProjectAccessInferenceService) {
        this.authResourceRepository = authResourceRepository;
        this.decisionService = decisionService;
        this.bootstrapOrganizationAccessAction = bootstrapOrganizationAccessAction;
        this.bootstrapWorkspaceAccessAction = bootstrapWorkspaceAccessAction;
        this.bootstrapProjectAccessAction = bootstrapProjectAccessAction;
        this.bootstrapTeamAccessAction = bootstrapTeamAccessAction;
        this.bootstrapOrganizationTeamAccessAction = bootstrapOrganizationTeamAccessAction;
        this.ensureWorkspaceMemberBaselineAccessAction = ensureWorkspaceMemberBaselineAccessAction;
        this.ensureProjectMemberBaselineAccessAction = ensureProjectMemberBaselineAccessAction;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
        this.memberProjectAccessInferenceService = memberProjectAccessInferenceService;
    }

    public void requireOrgAccess(UUID orgId, UUID userId, String rightCode) {
        IamAuthResource resource = resolveOrgResource(orgId);
        decisionService.requireAccess(new AuthorizationRequest(userId, resource.id(), rightCode));
    }

    public void requireOrgAccess(UUID orgId, UUID userId, IamPermissionAction authority) {
        IamAuthResource resource = resolveOrgResource(orgId);
        decisionService.requireAccess(new AuthorizationRequest(userId, resource.id(), authority));
    }

    public void requireWorkspaceAccess(UUID workspaceId, UUID userId, String rightCode) {
        IamAuthResource resource = resolveWorkspaceResource(workspaceId);
        decisionService.requireAccess(new AuthorizationRequest(userId, resource.id(), rightCode));
    }

    public void requireWorkspaceAccess(UUID workspaceId, UUID userId, IamPermissionAction authority) {
        IamAuthResource resource = resolveWorkspaceResource(workspaceId);
        decisionService.requireAccess(new AuthorizationRequest(userId, resource.id(), authority));
    }

    public boolean canWorkspaceAccess(UUID workspaceId, UUID userId, IamPermissionAction authority) {
        return authResourceRepository
                .findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE)
                .map(r -> decisionService.canAccess(new AuthorizationRequest(userId, r.id(), authority)).allowed())
                .orElse(false);
    }

    public boolean projectResourceExists(UUID projectId) {
        return authResourceRepository
                .findByRefIdAndResourceType(projectId, IamResourceType.PROJECT)
                .isPresent();
    }

    public boolean canProjectAccess(UUID projectId, UUID userId, IamPermissionAction authority) {
        return authResourceRepository
                .findByRefIdAndResourceType(projectId, IamResourceType.PROJECT)
                .map(r -> decisionService.canAccess(new AuthorizationRequest(userId, r.id(), authority)).allowed())
                .orElse(false);
    }

    /**
     * When a PROJECT IAM resource exists, authorize against it only (no workspace fallback).
     * Legacy projects without a PROJECT resource still fall back to WORKSPACE.
     */
    public void requireProjectOrWorkspaceAccess(
            UUID projectId, UUID workspaceId, UUID userId, IamPermissionAction authority) {
        var projectResource = authResourceRepository
                .findByRefIdAndResourceType(projectId, IamResourceType.PROJECT);
        if (projectResource.isPresent()) {
            decisionService.requireAccess(
                    new AuthorizationRequest(userId, projectResource.get().id(), authority));
            return;
        }
        requireWorkspaceAccess(workspaceId, userId, authority);
    }

    /**
     * True if the user may view the project: PROJECT grant when resource exists,
     * otherwise workspace PROJECT_VIEW (legacy).
     */
    public boolean canViewProject(UUID projectId, UUID workspaceId, UUID userId, IamPermissionAction viewAuthority) {
        if (projectResourceExists(projectId)) {
            return canProjectAccess(projectId, userId, viewAuthority);
        }
        return canWorkspaceAccess(workspaceId, userId, viewAuthority);
    }

    private IamAuthResource resolveOrgResource(UUID orgId) {
        return authResourceRepository.findByRefIdAndResourceType(orgId, IamResourceType.ORGANIZATION)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(orgId));
    }

    private IamAuthResource resolveWorkspaceResource(UUID workspaceId) {
        return authResourceRepository.findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(workspaceId));
    }

    public UUID bootstrapOrganizationAccess(UUID orgId, String orgName, UUID ownerUserId) {
        return bootstrapOrganizationAccessAction.execute(
                new BootstrapOrganizationAccessCommand(orgId, orgName, ownerUserId));
    }

    public UUID bootstrapWorkspaceAccess(UUID workspaceId, UUID organizationId,
                                         String workspaceName, UUID ownerUserId) {
        UUID resourceId = bootstrapWorkspaceAccessAction.execute(
                new BootstrapWorkspaceAccessCommand(workspaceId, organizationId, workspaceName, ownerUserId));
        // Owner policy may lag new productivity actions; attach baseline explicitly.
        ensureWorkspaceMemberBaselineAccess(workspaceId, ownerUserId);
        return resourceId;
    }

    /**
     * Workspace shell baseline + project-member baseline on every PROJECT in the workspace.
     */
    public void ensureWorkspaceMemberBaselineAccess(UUID workspaceId, UUID userId) {
        ensureWorkspaceMemberBaselineAccessAction.execute(workspaceId, userId);
        ensureProjectMemberBaselinesForUser(workspaceId, userId);
    }

    public UUID bootstrapProjectAccess(
            UUID projectId,
            String projectName,
            UUID workspaceId,
            UUID organizationId,
            UUID ownerUserId) {
        UUID resourceId = bootstrapProjectAccessAction.execute(new BootstrapProjectAccessCommand(
                projectId, projectName, workspaceId, organizationId, ownerUserId));
        ensureProjectMemberBaselinesForProject(projectId, workspaceId);
        return resourceId;
    }

    /** Attach project-member baseline for one user across all projects in a workspace. */
    public void ensureProjectMemberBaselinesForUser(UUID workspaceId, UUID userId) {
        for (Project project : projectRepository.findAllByWorkspaceId(workspaceId)) {
            ensureProjectMemberBaselineAccess(project.id(), userId);
        }
    }

    /** Attach project-member baseline for one user on one project (no-op if PROJECT resource missing). */
    public void ensureProjectMemberBaselineAccess(UUID projectId, UUID userId) {
        if (!projectResourceExists(projectId)) {
            return;
        }
        try {
            ensureProjectMemberBaselineAccessAction.execute(projectId, userId);
        } catch (Exception ex) {
            log.warn("Project member baseline failed user={} project={}: {}",
                    userId, projectId, ex.getMessage());
        }
    }

    /**
     * Attach project-member baseline for one user on one project.
     * Throws if the PROJECT IAM resource is missing or baseline attach fails.
     */
    public void ensureProjectMemberBaselineAccessStrict(UUID projectId, UUID userId) {
        if (!projectResourceExists(projectId)) {
            throw IamExceptions.iamAuthResourceNotFound(projectId);
        }
        ensureProjectMemberBaselineAccessAction.execute(projectId, userId);
    }

    /**
     * Attach project-member baseline for active workspace members who currently have full
     * workspace project access (ALL). CUSTOM-subset members are skipped for newly created projects.
     */
    public void ensureProjectMemberBaselinesForProject(UUID projectId, UUID workspaceId) {
        if (!projectResourceExists(projectId)) {
            return;
        }
        int page = 0;
        while (true) {
            var result = workspaceMemberRepository.findAll(
                    workspaceId, null, WorkspaceMemberStatus.ACTIVE, PageQuery.of(page, 100));
            for (WorkspaceMember member : result.content()) {
                if (!memberProjectAccessInferenceService.hasFullProjectAccess(
                        workspaceId, member.userId(), projectId)) {
                    continue;
                }
                try {
                    ensureProjectMemberBaselineAccessAction.execute(projectId, member.userId());
                } catch (Exception ex) {
                    log.warn("Project member baseline failed user={} project={}: {}",
                            member.userId(), projectId, ex.getMessage());
                }
            }
            if (result.content().size() < 100 || result.last()) {
                break;
            }
            page++;
        }
    }

    public UUID bootstrapTeamAccess(UUID teamId, UUID workspaceId, String teamName, UUID ownerUserId) {
        return bootstrapTeamAccessAction.execute(
                new BootstrapTeamAccessCommand(teamId, workspaceId, teamName, ownerUserId));
    }

    public UUID bootstrapOrganizationTeamAccess(UUID teamId, UUID organizationId,
                                                String teamName, UUID ownerUserId) {
        return bootstrapOrganizationTeamAccessAction.execute(
                new BootstrapOrganizationTeamAccessCommand(teamId, organizationId, teamName, ownerUserId));
    }
}
