package com.company.scopery.modules.project.project.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionCatalogResponse;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionsSnapshotResponse;
import com.company.scopery.modules.iam.grant.application.service.MemberPermissionsService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.member.http.request.ReplaceMemberPermissionsRequest;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Project - Member permissions", description = "Owner tick-grant permissions for members on a project")
@RestController
public class ProjectMemberPermissionsController {

    private final MemberPermissionsService memberPermissionsService;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public ProjectMemberPermissionsController(
            MemberPermissionsService memberPermissionsService,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.memberPermissionsService = memberPermissionsService;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Operation(summary = "Catalog of permissions the current user can grant on this project")
    @GetMapping(ProjectApiPaths.PROJECT_MEMBER_PERMISSIONS)
    public ResponseEntity<ApiResponse<MemberPermissionCatalogResponse>> catalog(
            @PathVariable UUID projectId) {
        requireProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.catalog(IamResourceType.PROJECT, projectId)));
    }

    @Operation(summary = "Permissions currently granted to a member on this project (subset of catalog)")
    @GetMapping(ProjectApiPaths.PROJECT_MEMBER_PERMISSIONS_BY_USER)
    public ResponseEntity<ApiResponse<MemberPermissionsSnapshotResponse>> snapshot(
            @PathVariable UUID projectId,
            @PathVariable UUID userId) {
        Project project = requireProject(projectId);
        requireActiveWorkspaceMember(project.workspaceId(), userId);
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.snapshotForUser(IamResourceType.PROJECT, projectId, userId)));
    }

    @Operation(summary = "Replace grantable permissions for a member on this project")
    @PutMapping(ProjectApiPaths.PROJECT_MEMBER_PERMISSIONS_BY_USER)
    public ResponseEntity<ApiResponse<MemberPermissionsSnapshotResponse>> replace(
            @PathVariable UUID projectId,
            @PathVariable UUID userId,
            @Valid @RequestBody ReplaceMemberPermissionsRequest request) {
        Project project = requireProject(projectId);
        requireActiveWorkspaceMember(project.workspaceId(), userId);
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.replaceForUser(
                        IamResourceType.PROJECT,
                        projectId,
                        userId,
                        request.permissionActionIds())));
    }

    private Project requireProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
    }

    private void requireActiveWorkspaceMember(UUID workspaceId, UUID userId) {
        if (!workspaceMemberRepository.isActiveMember(workspaceId, userId)) {
            throw WorkspaceExceptions.workspaceMemberNotFound(userId);
        }
    }
}
