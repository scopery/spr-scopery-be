package com.company.scopery.modules.workspace.workspace.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.workspace.api.request.CreateWorkspaceRequest;
import com.company.scopery.modules.workspace.workspace.api.request.UpdateWorkspaceRequest;
import com.company.scopery.modules.workspace.workspace.application.WorkspaceApplicationService;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.command.UpdateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.query.SearchWorkspaceQuery;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workspace - Workspaces", description = "Manage workspaces")
@RestController
@RequestMapping(WorkspaceApiPaths.WORKSPACES)
public class WorkspaceController {

    private final WorkspaceApplicationService workspaceApplicationService;

    public WorkspaceController(WorkspaceApplicationService workspaceApplicationService) {
        this.workspaceApplicationService = workspaceApplicationService;
    }

    @Operation(summary = "Create a new workspace")
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceDetailResponse>> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request) {
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(
                request.organizationId(), request.name(), request.code(),
                request.description(), request.defaultVisibility());
        WorkspaceDetailResponse response = workspaceApplicationService.createWorkspace(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing workspace")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> updateWorkspace(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkspaceRequest request) {
        UpdateWorkspaceCommand command = new UpdateWorkspaceCommand(
                id, request.name(), request.description(), request.defaultVisibility());
        WorkspaceResponse response = workspaceApplicationService.updateWorkspace(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get workspace detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceDetailResponse>> getWorkspace(@PathVariable UUID id) {
        WorkspaceDetailResponse response = workspaceApplicationService.getWorkspace(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list workspaces with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WorkspaceResponse>>> searchWorkspaces(
            @Parameter(description = "Filter by organization ID") @RequestParam(required = false) UUID organizationId,
            @Parameter(description = "Filter by owner user ID") @RequestParam(required = false) UUID ownerUserId,
            @Parameter(description = "Search by name or code") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, ARCHIVED)") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchWorkspaceQuery query = new SearchWorkspaceQuery(organizationId, ownerUserId, keyword, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                workspaceApplicationService.searchWorkspaces(query))));
    }

    @Operation(summary = "Activate a workspace")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> activateWorkspace(@PathVariable UUID id) {
        WorkspaceResponse response = workspaceApplicationService.activateWorkspace(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Archive a workspace")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> archiveWorkspace(@PathVariable UUID id) {
        WorkspaceResponse response = workspaceApplicationService.archiveWorkspace(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
