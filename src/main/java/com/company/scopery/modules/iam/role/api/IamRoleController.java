package com.company.scopery.modules.iam.role.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.role.api.request.CreateIamRoleRequest;
import com.company.scopery.modules.iam.role.api.request.UpdateIamRoleRequest;
import com.company.scopery.modules.iam.role.application.IamRoleApplicationService;
import com.company.scopery.modules.iam.role.application.command.CreateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.UpdateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.query.SearchIamRoleQuery;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "IAM - Roles", description = "Manage IAM roles")
@RestController
@RequestMapping(IamApiPaths.IAM_ROLES)
public class IamRoleController {

    private final IamRoleApplicationService service;

    public IamRoleController(IamRoleApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create a system-scoped role")
    @PostMapping("/system")
    public ResponseEntity<ApiResponse<IamRoleResponse>> createSystemRole(
            @Valid @RequestBody CreateIamRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.createSystemRole(toCommand(request))));
    }

    @Operation(summary = "Create a workspace-scoped role")
    @PostMapping("/workspace")
    public ResponseEntity<ApiResponse<IamRoleResponse>> createWorkspaceRole(
            @Valid @RequestBody CreateIamRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.createWorkspaceRole(toCommand(request))));
    }

    @Operation(summary = "Update role name and description")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRoleResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIamRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.updateRole(new UpdateIamRoleCommand(id, request.name(), request.description()))));
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRoleResponse>> getRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getRole(id)));
    }

    @Operation(summary = "Search roles")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamRoleResponse>>> searchRoles(
            @Parameter(description = "Search by code or name") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by workspace ID") @RequestParam(required = false) UUID workspaceId,
            @Parameter(description = "Filter by role scope (SYSTEM, WORKSPACE)") @RequestParam(required = false) String roleScope,
            @Parameter(description = "Filter by role source (SYSTEM_BUILT_IN, SYSTEM_TEMPLATE, WORKSPACE_CUSTOM)") @RequestParam(required = false) String roleSource,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DELETED)") @RequestParam(required = false) String status,
            @Parameter(description = "Include soft-deleted roles") @RequestParam(defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                service.searchRoles(new SearchIamRoleQuery(keyword, workspaceId, roleScope, roleSource, status, includeDeleted, page, size)))));
    }

    @Operation(summary = "Activate role")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamRoleResponse>> activateRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.activateRole(id)));
    }

    @Operation(summary = "Deactivate role")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamRoleResponse>> deactivateRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.deactivateRole(id)));
    }

    @Operation(summary = "Soft-delete a role")
    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<ApiResponse<IamRoleResponse>> softDeleteRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.softDeleteRole(id)));
    }

    private CreateIamRoleCommand toCommand(CreateIamRoleRequest r) {
        return new CreateIamRoleCommand(r.code(), r.name(), r.description(),
                r.roleScope(), r.roleSource(), r.workspaceId(), r.parentRoleId());
    }
}
