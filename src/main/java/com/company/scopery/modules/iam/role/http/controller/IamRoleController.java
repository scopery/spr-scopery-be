package com.company.scopery.modules.iam.role.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.role.application.action.ActivateIamRoleAction;
import com.company.scopery.modules.iam.role.application.action.CreateSystemIamRoleAction;
import com.company.scopery.modules.iam.role.application.action.CreateWorkspaceIamRoleAction;
import com.company.scopery.modules.iam.role.application.action.DeactivateIamRoleAction;
import com.company.scopery.modules.iam.role.application.action.SoftDeleteIamRoleAction;
import com.company.scopery.modules.iam.role.application.action.UpdateIamRoleAction;
import com.company.scopery.modules.iam.role.application.command.ActivateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.CreateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.DeactivateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.SoftDeleteIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.UpdateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.query.SearchIamRoleQuery;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.application.service.IamRoleQueryService;
import com.company.scopery.modules.iam.role.http.request.CreateIamRoleRequest;
import com.company.scopery.modules.iam.role.http.request.SearchIamRoleRequest;
import com.company.scopery.modules.iam.role.http.request.UpdateIamRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "IAM - Roles", description = "Manage IAM roles")
@RestController
@RequestMapping(IamApiPaths.IAM_ROLES)
public class IamRoleController {

    private final CreateSystemIamRoleAction createSystemAction;
    private final CreateWorkspaceIamRoleAction createWorkspaceAction;
    private final UpdateIamRoleAction updateAction;
    private final ActivateIamRoleAction activateAction;
    private final DeactivateIamRoleAction deactivateAction;
    private final SoftDeleteIamRoleAction softDeleteAction;
    private final IamRoleQueryService queryService;

    public IamRoleController(CreateSystemIamRoleAction createSystemAction,
                             CreateWorkspaceIamRoleAction createWorkspaceAction,
                             UpdateIamRoleAction updateAction,
                             ActivateIamRoleAction activateAction,
                             DeactivateIamRoleAction deactivateAction,
                             SoftDeleteIamRoleAction softDeleteAction,
                             IamRoleQueryService queryService) {
        this.createSystemAction = createSystemAction;
        this.createWorkspaceAction = createWorkspaceAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.softDeleteAction = softDeleteAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a system-scoped role")
    @PostMapping("/system")
    public ResponseEntity<ApiResponse<IamRoleResponse>> createSystemRole(
            @Valid @RequestBody CreateIamRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                createSystemAction.execute(toCommand(request))));
    }

    @Operation(summary = "Create a workspace-scoped role")
    @PostMapping("/workspace")
    public ResponseEntity<ApiResponse<IamRoleResponse>> createWorkspaceRole(
            @Valid @RequestBody CreateIamRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                createWorkspaceAction.execute(toCommand(request))));
    }

    @Operation(summary = "Update role name and description")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRoleResponse>> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIamRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateAction.execute(new UpdateIamRoleCommand(id, request.name(), request.description()))));
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRoleResponse>> getRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getRole(id)));
    }

    @Operation(summary = "Search roles")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamRoleResponse>>> searchRoles(
            @ModelAttribute SearchIamRoleRequest request) {
        PageResult<IamRoleResponse> result = queryService.searchRoles(new SearchIamRoleQuery(
                request.keyword(), request.workspaceId(), request.roleScope(), request.roleSource(),
                request.status(), request.includeDeleted(), request.page(), request.size()));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate role")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamRoleResponse>> activateRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(new ActivateIamRoleCommand(id))));
    }

    @Operation(summary = "Deactivate role")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamRoleResponse>> deactivateRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(new DeactivateIamRoleCommand(id))));
    }

    @Operation(summary = "Soft-delete a role")
    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<ApiResponse<IamRoleResponse>> softDeleteRole(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(softDeleteAction.execute(new SoftDeleteIamRoleCommand(id))));
    }

    private CreateIamRoleCommand toCommand(CreateIamRoleRequest r) {
        return new CreateIamRoleCommand(r.code(), r.name(), r.description(),
                r.roleScope(), r.roleSource(), r.workspaceId(), r.parentRoleId());
    }
}
