package com.company.scopery.modules.workspace.member.application.service;

import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.workspace.member.application.response.MemberProjectAccessSnapshot;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberAccessResponse;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Query + replace workspace member project access (Full workspace vs Custom).
 */
@Service
public class MemberProjectAccessService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final MemberProjectAccessInferenceService inferenceService;
    private final IamActivityLogger iamActivityLogger;

    public MemberProjectAccessService(WorkspaceRepository workspaceRepository,
                                      WorkspaceMemberRepository workspaceMemberRepository,
                                      ProjectRepository projectRepository,
                                      IamAccessGrantRepository grantRepository,
                                      IamAuthResourceRepository resourceRepository,
                                      CurrentUserAuthorizationService currentUserService,
                                      WorkspaceIamIntegrationService iamIntegrationService,
                                      MemberProjectAccessInferenceService inferenceService,
                                      IamActivityLogger iamActivityLogger) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.projectRepository = projectRepository;
        this.grantRepository = grantRepository;
        this.resourceRepository = resourceRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.inferenceService = inferenceService;
        this.iamActivityLogger = iamActivityLogger;
    }

    @Transactional(readOnly = true)
    public WorkspaceMemberAccessResponse getAccess(UUID workspaceId, UUID userId) {
        requireWorkspaceManage(workspaceId);
        workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(workspaceId));
        return toResponse(inferenceService.buildSnapshot(workspaceId, userId));
    }

    @Transactional
    public WorkspaceMemberAccessResponse replaceAccess(UUID workspaceId, UUID userId, String mode,
                                                       List<UUID> projectIds) {
        requireWorkspaceManage(workspaceId);
        workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(workspaceId));

        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> WorkspaceExceptions.workspaceMemberNotFound(userId));
        if (member.status() != WorkspaceMemberStatus.ACTIVE) {
            throw new ValidationException("User is not an active workspace member");
        }

        String normalizedMode = mode == null ? null : mode.trim().toUpperCase();
        if (MemberProjectAccessSnapshot.MODE_ALL.equals(normalizedMode)) {
            iamIntegrationService.ensureProjectMemberBaselinesForUser(workspaceId, userId);
        } else if (MemberProjectAccessSnapshot.MODE_CUSTOM.equals(normalizedMode)) {
            if (projectIds == null) {
                throw new ValidationException("projectIds is required when mode=CUSTOM");
            }
            applyCustomAccess(workspaceId, userId, projectIds);
        } else {
            throw new ValidationException("mode must be ALL or CUSTOM");
        }

        return toResponse(inferenceService.buildSnapshot(workspaceId, userId));
    }

    private void applyCustomAccess(UUID workspaceId, UUID userId, List<UUID> projectIds) {
        List<Project> allProjects = projectRepository.findAllByWorkspaceId(workspaceId);
        Set<UUID> workspaceProjectIds = new HashSet<>();
        for (Project project : allProjects) {
            workspaceProjectIds.add(project.id());
        }

        Set<UUID> selected = new LinkedHashSet<>();
        for (UUID projectId : projectIds) {
            if (projectId == null) {
                continue;
            }
            if (!workspaceProjectIds.contains(projectId)) {
                throw ProjectExceptions.projectNotFound(projectId);
            }
            selected.add(projectId);
        }

        for (UUID projectId : selected) {
            iamIntegrationService.ensureProjectMemberBaselineAccessStrict(projectId, userId);
        }

        for (Project project : allProjects) {
            if (selected.contains(project.id())) {
                continue;
            }
            revokeUserGrantsOnProject(project.id(), userId);
        }
    }

    private void revokeUserGrantsOnProject(UUID projectId, UUID userId) {
        resourceRepository.findByRefIdAndResourceType(projectId, IamResourceType.PROJECT).ifPresent(resource ->
                grantRepository.findActiveBySubjectsAndResource(
                                List.of(IamSubjectType.USER), List.of(userId), resource.id())
                        .forEach(grant -> revokeGrant(grant)));
    }

    private void revokeGrant(IamAccessGrant grant) {
        IamAccessGrant saved = grantRepository.save(grant.revoke());
        iamActivityLogger.logSuccess(
                IamEntityTypes.IAM_ACCESS_GRANT,
                saved.id(),
                IamActivityActions.REVOKE_IAM_ACCESS_GRANT,
                "Access grant revoked by member project-access replace: " + saved.id());
    }

    private void requireWorkspaceManage(UUID workspaceId) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(workspaceId, actorId, IamAuthorities.WORKSPACE_MANAGE);
    }

    static WorkspaceMemberAccessResponse toResponse(MemberProjectAccessSnapshot snapshot) {
        return new WorkspaceMemberAccessResponse(
                snapshot.workspaceId(),
                snapshot.userId(),
                snapshot.accessMode(),
                snapshot.totalProjects(),
                mapItems(snapshot.projects()),
                mapItems(snapshot.availableProjects()));
    }

    private static List<WorkspaceMemberAccessResponse.ProjectAccessItem> mapItems(
            List<MemberProjectAccessSnapshot.ProjectAccessItem> items) {
        return items.stream()
                .map(p -> new WorkspaceMemberAccessResponse.ProjectAccessItem(
                        p.projectId(), p.projectName(), p.projectCode()))
                .toList();
    }
}
