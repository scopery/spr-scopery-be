package com.company.scopery.modules.iam.roleassignment.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.roleassignment.application.action.ActivateIamRoleAssignmentAction;
import com.company.scopery.modules.iam.roleassignment.application.action.AssignIamRoleAction;
import com.company.scopery.modules.iam.roleassignment.application.action.DeactivateIamRoleAssignmentAction;
import com.company.scopery.modules.iam.roleassignment.application.command.ActivateIamRoleAssignmentCommand;
import com.company.scopery.modules.iam.roleassignment.application.command.AssignRoleCommand;
import com.company.scopery.modules.iam.roleassignment.application.command.DeactivateIamRoleAssignmentCommand;
import com.company.scopery.modules.iam.roleassignment.application.query.SearchRoleAssignmentQuery;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.roleassignment.application.service.IamRoleAssignmentQueryService;
import com.company.scopery.modules.iam.roleassignment.http.request.AssignRoleRequest;
import com.company.scopery.modules.iam.roleassignment.http.request.SearchRoleAssignmentRequest;
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

    private final AssignIamRoleAction assignAction;
    private final ActivateIamRoleAssignmentAction activateAction;
    private final DeactivateIamRoleAssignmentAction deactivateAction;
    private final IamRoleAssignmentQueryService queryService;

    public IamRoleAssignmentController(AssignIamRoleAction assignAction,
                                       ActivateIamRoleAssignmentAction activateAction,
                                       DeactivateIamRoleAssignmentAction deactivateAction,
                                       IamRoleAssignmentQueryService queryService) {
        this.assignAction = assignAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Assign a role to a user or team")
    @PostMapping
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> assignRole(
            @Valid @RequestBody AssignRoleRequest request) {
        var command = new AssignRoleCommand(
                request.assigneeType(), request.assigneeId(),
                request.roleId(), request.workspaceId());
        return ResponseEntity.ok(ApiResponse.success(assignAction.execute(command)));
    }

    @Operation(summary = "Get role assignment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> getAssignment(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getAssignment(id)));
    }

    @Operation(summary = "Activate a role assignment")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(new ActivateIamRoleAssignmentCommand(id))));
    }

    @Operation(summary = "Deactivate a role assignment")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamRoleAssignmentResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(new DeactivateIamRoleAssignmentCommand(id))));
    }

    @Operation(summary = "Search role assignments")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamRoleAssignmentResponse>>> search(
            @ModelAttribute SearchRoleAssignmentRequest request) {
        var query = new SearchRoleAssignmentQuery(
                request.roleId(), request.assigneeId(), request.assigneeType(),
                request.status(), request.workspaceId(), request.page(), request.size());
        PageResult<IamRoleAssignmentResponse> result = queryService.searchAssignments(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }
}
