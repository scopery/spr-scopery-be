package com.company.scopery.modules.project.templatewbs.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.templatewbs.application.action.CreateProjectTemplateWbsNodeAction;
import com.company.scopery.modules.project.templatewbs.application.action.DeleteProjectTemplateWbsNodeAction;
import com.company.scopery.modules.project.templatewbs.application.action.MoveProjectTemplateWbsNodeAction;
import com.company.scopery.modules.project.templatewbs.application.action.UpdateProjectTemplateWbsNodeAction;
import com.company.scopery.modules.project.templatewbs.application.command.CreateProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.application.command.DeleteProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.application.command.MoveProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.application.command.UpdateProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.application.response.ProjectTemplateWbsNodeResponse;
import com.company.scopery.modules.project.templatewbs.application.service.ProjectTemplateWbsNodeQueryService;
import com.company.scopery.modules.project.templatewbs.http.request.CreateProjectTemplateWbsNodeRequest;
import com.company.scopery.modules.project.templatewbs.http.request.MoveProjectTemplateWbsNodeRequest;
import com.company.scopery.modules.project.templatewbs.http.request.UpdateProjectTemplateWbsNodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Project - Template WBS")
public class ProjectTemplateWbsNodeController {

    private final CreateProjectTemplateWbsNodeAction createAction;
    private final UpdateProjectTemplateWbsNodeAction updateAction;
    private final MoveProjectTemplateWbsNodeAction moveAction;
    private final DeleteProjectTemplateWbsNodeAction deleteAction;
    private final ProjectTemplateWbsNodeQueryService queryService;

    public ProjectTemplateWbsNodeController(CreateProjectTemplateWbsNodeAction createAction,
                                            UpdateProjectTemplateWbsNodeAction updateAction,
                                            MoveProjectTemplateWbsNodeAction moveAction,
                                            DeleteProjectTemplateWbsNodeAction deleteAction,
                                            ProjectTemplateWbsNodeQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.moveAction = moveAction;
        this.deleteAction = deleteAction;
        this.queryService = queryService;
    }

    @PostMapping(ProjectApiPaths.PROJECT_TEMPLATE_WBS_NODES)
    @Operation(summary = "Create a template WBS node (DRAFT version only)")
    public ResponseEntity<ApiResponse<ProjectTemplateWbsNodeResponse>> create(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @Valid @RequestBody CreateProjectTemplateWbsNodeRequest request) {
        ProjectTemplateWbsNodeResponse response = createAction.execute(new CreateProjectTemplateWbsNodeCommand(
                templateId, versionId, request.parentId(), request.templatePhaseId(),
                request.code(), request.title(), request.description(), request.nodeType(),
                request.orderIndex(), request.deliverableDocumentTypeId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping(ProjectApiPaths.PROJECT_TEMPLATE_WBS_NODES)
    @Operation(summary = "List template WBS nodes for a version")
    public ApiResponse<List<ProjectTemplateWbsNodeResponse>> list(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.listNodes(templateId, versionId));
    }

    @GetMapping(ProjectApiPaths.PROJECT_TEMPLATE_WBS_TREE)
    @Operation(summary = "Get template WBS tree (depth-ordered flat list)")
    public ApiResponse<List<ProjectTemplateWbsNodeResponse>> tree(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.getTree(templateId, versionId));
    }

    @GetMapping(ProjectApiPaths.PROJECT_TEMPLATE_WBS_NODES + "/{nodeId}")
    @Operation(summary = "Get a template WBS node")
    public ApiResponse<ProjectTemplateWbsNodeResponse> get(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID nodeId) {
        return ApiResponse.success(queryService.getNode(templateId, versionId, nodeId));
    }

    @PutMapping(ProjectApiPaths.PROJECT_TEMPLATE_WBS_NODES + "/{nodeId}")
    @Operation(summary = "Update a template WBS node (DRAFT version only)")
    public ApiResponse<ProjectTemplateWbsNodeResponse> update(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID nodeId,
            @Valid @RequestBody UpdateProjectTemplateWbsNodeRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateProjectTemplateWbsNodeCommand(
                templateId, versionId, nodeId, request.templatePhaseId(), request.code(),
                request.title(), request.description(), request.nodeType(),
                request.deliverableDocumentTypeId())));
    }

    @PatchMapping(ProjectApiPaths.PROJECT_TEMPLATE_WBS_NODES + "/{nodeId}/move")
    @Operation(summary = "Move a template WBS node (DRAFT version only)")
    public ApiResponse<ProjectTemplateWbsNodeResponse> move(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID nodeId,
            @Valid @RequestBody MoveProjectTemplateWbsNodeRequest request) {
        return ApiResponse.success(moveAction.execute(new MoveProjectTemplateWbsNodeCommand(
                templateId, versionId, nodeId, request.newParentId(), request.newOrderIndex())));
    }

    @DeleteMapping(ProjectApiPaths.PROJECT_TEMPLATE_WBS_NODES + "/{nodeId}")
    @Operation(summary = "Delete a template WBS node (DRAFT version only)")
    public ApiResponse<Void> delete(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID nodeId,
            @RequestParam(defaultValue = "false") boolean cascade) {
        deleteAction.execute(new DeleteProjectTemplateWbsNodeCommand(templateId, versionId, nodeId, cascade));
        return ApiResponse.success(null);
    }
}
