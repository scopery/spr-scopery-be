package com.company.scopery.modules.workspace.orgmember.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionCatalogResponse;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionsSnapshotResponse;
import com.company.scopery.modules.iam.grant.application.service.MemberPermissionsService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.workspace.member.http.request.ReplaceMemberPermissionsRequest;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Organization - Member permissions", description = "Owner tick-grant permissions for organization members")
@RestController
public class OrgMemberPermissionsController {

    private final MemberPermissionsService memberPermissionsService;
    private final OrgMemberRepository orgMemberRepository;

    public OrgMemberPermissionsController(
            MemberPermissionsService memberPermissionsService,
            OrgMemberRepository orgMemberRepository) {
        this.memberPermissionsService = memberPermissionsService;
        this.orgMemberRepository = orgMemberRepository;
    }

    @Operation(summary = "Catalog of permissions the current user can grant on this organization")
    @GetMapping(WorkspaceApiPaths.ORGANIZATION_MEMBER_PERMISSIONS)
    public ResponseEntity<ApiResponse<MemberPermissionCatalogResponse>> catalog(
            @PathVariable UUID organizationId) {
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.catalog(IamResourceType.ORGANIZATION, organizationId)));
    }

    @Operation(summary = "Permissions currently granted to an organization member (subset of catalog)")
    @GetMapping(WorkspaceApiPaths.ORG_MEMBERS + "/by-user/{userId}/permissions")
    public ResponseEntity<ApiResponse<MemberPermissionsSnapshotResponse>> snapshot(
            @PathVariable UUID organizationId,
            @PathVariable UUID userId) {
        requireActiveMember(organizationId, userId);
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.snapshotForUser(IamResourceType.ORGANIZATION, organizationId, userId)));
    }

    @Operation(summary = "Replace grantable permissions for an organization member")
    @PutMapping(WorkspaceApiPaths.ORG_MEMBERS + "/by-user/{userId}/permissions")
    public ResponseEntity<ApiResponse<MemberPermissionsSnapshotResponse>> replace(
            @PathVariable UUID organizationId,
            @PathVariable UUID userId,
            @Valid @RequestBody ReplaceMemberPermissionsRequest request) {
        requireActiveMember(organizationId, userId);
        return ResponseEntity.ok(ApiResponse.success(
                memberPermissionsService.replaceForUser(
                        IamResourceType.ORGANIZATION,
                        organizationId,
                        userId,
                        request.permissionActionIds())));
    }

    private void requireActiveMember(UUID organizationId, UUID userId) {
        if (!orgMemberRepository.isActiveMember(organizationId, userId)) {
            throw WorkspaceExceptions.orgMemberNotFound(userId);
        }
    }
}
