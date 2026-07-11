package com.company.scopery.modules.project.shared.authorization;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Enforces workspace-scoped IAM permissions for project module operations.
 * All project data lives under a workspace; access is checked against the workspace IAM resource.
 */
@Component
public class ProjectWorkspaceAuthorizationService {

    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ProjectRepository projectRepository;

    public ProjectWorkspaceAuthorizationService(CurrentUserAuthorizationService currentUserService,
                                                   WorkspaceIamIntegrationService iamIntegrationService,
                                                   ProjectRepository projectRepository) {
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.projectRepository = projectRepository;
    }

    public void requireWorkspacePermission(UUID workspaceId, IamPermissionAction authority) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(workspaceId, userId, authority);
    }

    public void requireProjectPermission(UUID projectId, IamPermissionAction authority) {
        UUID workspaceId = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId))
                .workspaceId();
        requireWorkspacePermission(workspaceId, authority);
    }
}
