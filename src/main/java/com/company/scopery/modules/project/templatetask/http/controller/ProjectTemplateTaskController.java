package com.company.scopery.modules.project.templatetask.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.templatetask.application.action.CreateProjectTemplateTaskAction;
import com.company.scopery.modules.project.templatetask.application.action.DeleteProjectTemplateTaskAction;
import com.company.scopery.modules.project.templatetask.application.action.UpdateProjectTemplateTaskAction;
import com.company.scopery.modules.project.templatetask.application.command.CreateProjectTemplateTaskCommand;
import com.company.scopery.modules.project.templatetask.application.command.DeleteProjectTemplateTaskCommand;
import com.company.scopery.modules.project.templatetask.application.command.UpdateProjectTemplateTaskCommand;
import com.company.scopery.modules.project.templatetask.application.response.ProjectTemplateTaskResponse;
import com.company.scopery.modules.project.templatetask.application.service.ProjectTemplateTaskQueryService;
import com.company.scopery.modules.project.templatetask.http.request.CreateProjectTemplateTaskRequest;
import com.company.scopery.modules.project.templatetask.http.request.UpdateProjectTemplateTaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.PROJECT_TEMPLATE_TASKS)
@Tag(name = "Project - Template Tasks")
public class ProjectTemplateTaskController {

    private final CreateProjectTemplateTaskAction createAction;
    private final UpdateProjectTemplateTaskAction updateAction;
    private final DeleteProjectTemplateTaskAction deleteAction;
    private final ProjectTemplateTaskQueryService queryService;

    public ProjectTemplateTaskController(CreateProjectTemplateTaskAction createAction,
                                         UpdateProjectTemplateTaskAction updateAction,
                                         DeleteProjectTemplateTaskAction deleteAction,
                                         ProjectTemplateTaskQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a template task (DRAFT version only)")
    public ResponseEntity<ApiResponse<ProjectTemplateTaskResponse>> create(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @Valid @RequestBody CreateProjectTemplateTaskRequest request) {
        ProjectTemplateTaskResponse response = createAction.execute(new CreateProjectTemplateTaskCommand(
                templateId, versionId, request.templatePhaseId(), request.templateWbsNodeId(),
                request.code(), request.title(), request.description(), request.defaultPriority(),
                request.estimateHours(), request.dueOffsetDays(), request.startOffsetDays(),
                request.defaultAssigneeRoleCode(), request.deliverableDocumentTypeId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List template tasks for a version")
    public ApiResponse<List<ProjectTemplateTaskResponse>> list(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.listTasks(templateId, versionId));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a template task")
    public ApiResponse<ProjectTemplateTaskResponse> get(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID taskId) {
        return ApiResponse.success(queryService.getTask(templateId, versionId, taskId));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update a template task (DRAFT version only)")
    public ApiResponse<ProjectTemplateTaskResponse> update(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateProjectTemplateTaskRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateProjectTemplateTaskCommand(
                templateId, versionId, taskId, request.templatePhaseId(), request.templateWbsNodeId(),
                request.code(), request.title(), request.description(), request.defaultPriority(),
                request.estimateHours(), request.dueOffsetDays(), request.startOffsetDays(),
                request.defaultAssigneeRoleCode(), request.deliverableDocumentTypeId())));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a template task (DRAFT version only)")
    public ApiResponse<Void> delete(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID taskId) {
        deleteAction.execute(new DeleteProjectTemplateTaskCommand(templateId, versionId, taskId));
        return ApiResponse.success(null);
    }
}
