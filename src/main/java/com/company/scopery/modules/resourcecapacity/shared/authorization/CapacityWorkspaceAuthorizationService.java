package com.company.scopery.modules.resourcecapacity.shared.authorization;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Workspace-scoped IAM checks for Capacity module.
 *
 * <p>Simplifications (Phase 12):
 * <ul>
 *   <li>{@code CAPACITY_OVERVIEW_VIEW} / {@code CAPACITY_OVERALLOCATION_VIEW} → {@link IamAuthorities#CAPACITY_VIEW}</li>
 * </ul>
 */
@Component
public class CapacityWorkspaceAuthorizationService {

    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ProjectRepository projectRepository;

    public CapacityWorkspaceAuthorizationService(CurrentUserAuthorizationService currentUserService,
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

    public void requireProjectWorkspacePermission(UUID projectId, IamPermissionAction authority) {
        UUID workspaceId = projectRepository.findById(projectId)
                .orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId))
                .workspaceId();
        requireWorkspacePermission(workspaceId, authority);
    }

    public void requireCalendarView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_CALENDAR_VIEW);
    }

    public void requireCalendarCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_CALENDAR_CREATE);
    }

    public void requireCalendarUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_CALENDAR_UPDATE);
    }

    public void requireCalendarArchive(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_CALENDAR_ARCHIVE);
    }

    public void requireProfileView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_VIEW);
    }

    public void requireProfileCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_CREATE);
    }

    public void requireProfileUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_UPDATE);
    }

    public void requireProfileArchive(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_ARCHIVE);
    }

    public void requireAllocationView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_ALLOCATION_VIEW);
    }

    public void requireAllocationCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_ALLOCATION_CREATE);
    }

    public void requireAllocationUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_ALLOCATION_UPDATE);
    }

    public void requireAllocationArchive(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_ALLOCATION_ARCHIVE);
    }

    public void requireCapacityView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_VIEW);
    }

    public void requireCapacityCalculate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_CALCULATE);
    }
}
