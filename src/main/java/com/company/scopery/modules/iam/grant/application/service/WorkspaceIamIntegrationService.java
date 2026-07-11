package com.company.scopery.modules.iam.grant.application.service;

import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.application.action.BootstrapOrganizationAccessAction;
import com.company.scopery.modules.iam.grant.application.action.BootstrapOrganizationTeamAccessAction;
import com.company.scopery.modules.iam.grant.application.action.BootstrapTeamAccessAction;
import com.company.scopery.modules.iam.grant.application.action.BootstrapWorkspaceAccessAction;
import com.company.scopery.modules.iam.grant.application.command.BootstrapOrganizationAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.BootstrapOrganizationTeamAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.BootstrapTeamAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.BootstrapWorkspaceAccessCommand;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Facade for workspace module integration: bootstraps IAM resources/grants and enforces access guards.
 */
@Service
public class WorkspaceIamIntegrationService {

    private final IamAuthResourceRepository authResourceRepository;
    private final AuthorizationDecisionService decisionService;
    private final BootstrapOrganizationAccessAction bootstrapOrganizationAccessAction;
    private final BootstrapWorkspaceAccessAction bootstrapWorkspaceAccessAction;
    private final BootstrapTeamAccessAction bootstrapTeamAccessAction;
    private final BootstrapOrganizationTeamAccessAction bootstrapOrganizationTeamAccessAction;

    public WorkspaceIamIntegrationService(IamAuthResourceRepository authResourceRepository,
                                          AuthorizationDecisionService decisionService,
                                          BootstrapOrganizationAccessAction bootstrapOrganizationAccessAction,
                                          BootstrapWorkspaceAccessAction bootstrapWorkspaceAccessAction,
                                          BootstrapTeamAccessAction bootstrapTeamAccessAction,
                                          BootstrapOrganizationTeamAccessAction bootstrapOrganizationTeamAccessAction) {
        this.authResourceRepository = authResourceRepository;
        this.decisionService = decisionService;
        this.bootstrapOrganizationAccessAction = bootstrapOrganizationAccessAction;
        this.bootstrapWorkspaceAccessAction = bootstrapWorkspaceAccessAction;
        this.bootstrapTeamAccessAction = bootstrapTeamAccessAction;
        this.bootstrapOrganizationTeamAccessAction = bootstrapOrganizationTeamAccessAction;
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
        return bootstrapWorkspaceAccessAction.execute(
                new BootstrapWorkspaceAccessCommand(workspaceId, organizationId, workspaceName, ownerUserId));
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
