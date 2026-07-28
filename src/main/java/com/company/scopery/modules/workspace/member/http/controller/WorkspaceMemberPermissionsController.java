package com.company.scopery.modules.workspace.member.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionCatalogResponse;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionsSnapshotResponse;
import com.company.scopery.modules.iam.grant.application.service.MemberPermissionsService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.member.http.request.ReplaceMemberPermissionsRequest;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workspace - Member permissions", description = "Owner tick-grant permissions for workspace members")
@RestController
public class WorkspaceMemberPermissionsController {

    private final MemberPermissionsService memberPermissionsService;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceMemberPermissionsController(
            MemberPermissionsService memberPermissionsService,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.memberPermissionsService = memberPermissionsService;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Operation(summary = "Catalog of permissions the current user can grant on this workspace")
    @GetMapping(WorkspaceApiPaths.WORKSPACE_MEMBER_PERMISSIONS)
    public ResponseEntity<ApiResponse<MemberPermissionCatalogResponse>> catalog(
            @PathVariable UUID workspaceId) {
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.catalog(IamResourceType.WORKSPACE, workspaceId)));
    }

    @Operation(summary = "Permissions currently granted to a workspace member (subset of catalog)")
    @GetMapping(WorkspaceApiPaths.WORKSPACE_MEMBERS + "/by-user/{userId}/permissions")
    public ResponseEntity<ApiResponse<MemberPermissionsSnapshotResponse>> snapshot(
            @PathVariable UUID workspaceId,
            @PathVariable UUID userId) {
        requireActiveMember(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.snapshotForUser(IamResourceType.WORKSPACE, workspaceId, userId)));
    }

    @Operation(summary = "Replace grantable permissions for a workspace member")
    @PutMapping(WorkspaceApiPaths.WORKSPACE_MEMBERS + "/by-user/{userId}/permissions")
    public ResponseEntity<ApiResponse<MemberPermissionsSnapshotResponse>> replace(
            @PathVariable UUID workspaceId,
            @PathVariable UUID userId,
            @Valid @RequestBody ReplaceMemberPermissionsRequest request) {
        requireActiveMember(workspaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.replaceForUser(
                        IamResourceType.WORKSPACE,
                        workspaceId,
                        userId,
                        request.permissionActionIds())));
    }

    private void requireActiveMember(UUID workspaceId, UUID userId) {
        if (!workspaceMemberRepository.isActiveMember(workspaceId, userId)) {
            throw WorkspaceExceptions.workspaceMemberNotFound(userId);
        }
    }
}
