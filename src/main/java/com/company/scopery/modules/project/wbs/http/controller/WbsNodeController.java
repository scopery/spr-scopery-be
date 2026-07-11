package com.company.scopery.modules.project.wbs.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.wbs.application.action.ArchiveWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.CreateWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.MoveWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.action.UpdateWbsNodeAction;
import com.company.scopery.modules.project.wbs.application.command.ArchiveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.CreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.MoveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.UpdateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.query.SearchWbsNodeQuery;
import com.company.scopery.modules.project.wbs.application.response.WbsNodeResponse;
import com.company.scopery.modules.project.wbs.application.service.WbsNodeQueryService;
import com.company.scopery.modules.project.wbs.http.request.CreateWbsNodeRequest;
import com.company.scopery.modules.project.wbs.http.request.MoveWbsNodeRequest;
import com.company.scopery.modules.project.wbs.http.request.UpdateWbsNodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Project - WBS", description = "Manage WBS nodes within a project")
@RestController
@RequestMapping(ProjectApiPaths.WBS_NODES)
public class WbsNodeController {

    private final WbsNodeQueryService queryService;
    private final CreateWbsNodeAction createAction;
    private final UpdateWbsNodeAction updateAction;
    private final MoveWbsNodeAction moveAction;
    private final ArchiveWbsNodeAction archiveAction;

    public WbsNodeController(WbsNodeQueryService queryService,
                              CreateWbsNodeAction createAction,
                              UpdateWbsNodeAction updateAction,
                              MoveWbsNodeAction moveAction,
                              ArchiveWbsNodeAction archiveAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.moveAction = moveAction;
        this.archiveAction = archiveAction;
    }

    @Operation(summary = "Create a new WBS node")
    @PostMapping
    public ResponseEntity<ApiResponse<WbsNodeResponse>> createWbsNode(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateWbsNodeRequest request) {

        CreateWbsNodeCommand command = new CreateWbsNodeCommand(
                projectId,
                request.phaseId(),
                request.parentId(),
                request.code(),
                request.title(),
                request.description(),
                request.nodeType(),
                request.sortOrder()
        );

        WbsNodeResponse response = createAction.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Get a WBS node by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WbsNodeResponse>> getWbsNode(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

        WbsNodeResponse response = queryService.getWbsNode(projectId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search WBS nodes with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WbsNodeResponse>>> searchWbsNodes(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID phaseId,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchWbsNodeQuery query = new SearchWbsNodeQuery(projectId, phaseId, parentId, status, page, size);
        PageResult<WbsNodeResponse> result = queryService.searchWbsNodes(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Get full WBS tree for a project")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<WbsNodeResponse>>> getWbsTree(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID phaseId) {

        List<WbsNodeResponse> tree = queryService.getWbsTree(projectId, phaseId);
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    @Operation(summary = "Update a WBS node")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WbsNodeResponse>> updateWbsNode(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWbsNodeRequest request) {

        UpdateWbsNodeCommand command = new UpdateWbsNodeCommand(
                id, projectId, request.title(), request.description(), request.nodeType());

        WbsNodeResponse response = updateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Move a WBS node to a new parent and/or sort order")
    @PatchMapping("/{id}/move")
    public ResponseEntity<ApiResponse<WbsNodeResponse>> moveWbsNode(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody MoveWbsNodeRequest request) {

        MoveWbsNodeCommand command = new MoveWbsNodeCommand(
                id, projectId, request.newParentId(), request.newSortOrder());

        WbsNodeResponse response = moveAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Archive a WBS node")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<WbsNodeResponse>> archiveWbsNode(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

        ArchiveWbsNodeCommand command = new ArchiveWbsNodeCommand(id, projectId);
        WbsNodeResponse response = archiveAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
