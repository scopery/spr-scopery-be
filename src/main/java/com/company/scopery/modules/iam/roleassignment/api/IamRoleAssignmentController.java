package com.company.scopery.modules.iam.roleassignment.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.roleassignment.api.request.AssignRoleRequest;
import com.company.scopery.modules.iam.roleassignment.api.request.SearchRoleAssignmentRequest;
import com.company.scopery.modules.iam.roleassignment.application.IamRoleAssignmentApplicationService;
import com.company.scopery.modules.iam.roleassignment.application.command.AssignRoleCommand;
import com.company.scopery.modules.iam.roleassignment.application.query.SearchRoleAssignmentQuery;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "IAM - Role Assignments")
@RestController
@RequestMapping(IamApiPaths.IAM_ROLE_ASSIGNMENTS)
public class IamRoleAssignmentController {

    private final IamRoleAssignmentApplicationService service;

    public IamRoleAssignmentController(IamRoleAssignmentApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Assign a role to a user or team")
    @PostMapping
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> assignRole(
            @Valid @RequestBody AssignRoleRequest request) {
        var command = new AssignRoleCommand(
                request.assigneeType(), request.assigneeId(),
                request.roleId(), request.workspaceId(), request.assignedBy());
        return ResponseEntity.ok(ApiResponse.success(service.assignRole(command)));
    }

    @Operation(summary = "Get role assignment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> getAssignment(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getAssignment(id)));
    }

    @Operation(summary = "Activate a role assignment")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.activateAssignment(id)));
    }

    @Operation(summary = "Deactivate a role assignment")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.deactivateAssignment(id)));
    }

    @Operation(summary = "Search role assignments")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamRoleAssignmentResponse>>> search(
            @ModelAttribute SearchRoleAssignmentRequest request) {
        var query = new SearchRoleAssignmentQuery(
                request.roleId(), request.assigneeId(), request.assigneeType(),
                request.status(), request.workspaceId(), request.page(), request.size());
        return ResponseEntity.ok(ApiResponse.success(
                PageResponse.from(service.searchAssignments(query))));
    }
}
