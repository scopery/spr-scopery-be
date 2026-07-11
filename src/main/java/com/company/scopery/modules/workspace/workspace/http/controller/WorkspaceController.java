package com.company.scopery.modules.workspace.workspace.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import com.company.scopery.modules.workspace.workspace.application.action.ActivateWorkspaceAction;
import com.company.scopery.modules.workspace.workspace.application.action.ArchiveWorkspaceAction;
import com.company.scopery.modules.workspace.workspace.application.action.CreateWorkspaceAction;
import com.company.scopery.modules.workspace.workspace.application.action.UpdateWorkspaceAction;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.command.UpdateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.query.SearchWorkspaceQuery;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.application.service.WorkspaceQueryService;
import com.company.scopery.modules.workspace.workspace.http.request.CreateWorkspaceRequest;
import com.company.scopery.modules.workspace.workspace.http.request.UpdateWorkspaceRequest;
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

    private final CreateWorkspaceAction createAction;
    private final UpdateWorkspaceAction updateAction;
    private final ActivateWorkspaceAction activateAction;
    private final ArchiveWorkspaceAction archiveAction;
    private final WorkspaceQueryService queryService;

    public WorkspaceController(CreateWorkspaceAction createAction,
                                UpdateWorkspaceAction updateAction,
                                ActivateWorkspaceAction activateAction,
                                ArchiveWorkspaceAction archiveAction,
                                WorkspaceQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.archiveAction = archiveAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new workspace")
    @PostMapping
    public ResponseEntity<ApiResponse<WorkspaceDetailResponse>> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                createAction.execute(new CreateWorkspaceCommand(
                        request.organizationId(), request.name(), request.code(),
                        request.description(), request.defaultVisibility(), request.joinPolicy()))));
    }

    @Operation(summary = "Update an existing workspace")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> updateWorkspace(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkspaceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateAction.execute(new UpdateWorkspaceCommand(
                        id, request.name(), request.description(), request.defaultVisibility(), request.joinPolicy()))));
    }

    @Operation(summary = "Get workspace detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkspaceDetailResponse>> getWorkspace(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getWorkspace(id)));
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
        PageResult<WorkspaceResponse> result = queryService.searchWorkspaces(
                new SearchWorkspaceQuery(organizationId, ownerUserId, keyword, status, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate a workspace")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> activateWorkspace(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(id)));
    }

    @Operation(summary = "Archive a workspace")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<WorkspaceResponse>> archiveWorkspace(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(archiveAction.execute(id)));
    }
}
