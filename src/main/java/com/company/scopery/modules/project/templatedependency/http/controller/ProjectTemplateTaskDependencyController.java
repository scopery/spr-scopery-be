package com.company.scopery.modules.project.templatedependency.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.templatedependency.application.action.CreateProjectTemplateTaskDependencyAction;
import com.company.scopery.modules.project.templatedependency.application.action.RemoveProjectTemplateTaskDependencyAction;
import com.company.scopery.modules.project.templatedependency.application.command.CreateProjectTemplateTaskDependencyCommand;
import com.company.scopery.modules.project.templatedependency.application.command.RemoveProjectTemplateTaskDependencyCommand;
import com.company.scopery.modules.project.templatedependency.application.response.ProjectTemplateTaskDependencyResponse;
import com.company.scopery.modules.project.templatedependency.application.service.ProjectTemplateTaskDependencyQueryService;
import com.company.scopery.modules.project.templatedependency.http.request.CreateProjectTemplateTaskDependencyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.PROJECT_TEMPLATE_TASK_DEPENDENCIES)
@Tag(name = "Project - Template Task Dependencies")
public class ProjectTemplateTaskDependencyController {

    private final CreateProjectTemplateTaskDependencyAction createAction;
    private final RemoveProjectTemplateTaskDependencyAction removeAction;
    private final ProjectTemplateTaskDependencyQueryService queryService;

    public ProjectTemplateTaskDependencyController(
            CreateProjectTemplateTaskDependencyAction createAction,
            RemoveProjectTemplateTaskDependencyAction removeAction,
            ProjectTemplateTaskDependencyQueryService queryService) {
        this.createAction = createAction;
        this.removeAction = removeAction;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create a template task dependency (DRAFT version only)")
    public ResponseEntity<ApiResponse<ProjectTemplateTaskDependencyResponse>> create(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @Valid @RequestBody CreateProjectTemplateTaskDependencyRequest request) {
        int lagDays = request.lagDays() != null ? request.lagDays() : 0;
        ProjectTemplateTaskDependencyResponse response = createAction.execute(
                new CreateProjectTemplateTaskDependencyCommand(
                        templateId, versionId,
                        request.predecessorTemplateTaskId(),
                        request.successorTemplateTaskId(),
                        request.dependencyType(),
                        lagDays));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List template task dependencies for a version")
    public ApiResponse<List<ProjectTemplateTaskDependencyResponse>> list(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.listDependencies(templateId, versionId));
    }

    @DeleteMapping("/{dependencyId}")
    @Operation(summary = "Remove a template task dependency (DRAFT version only)")
    public ApiResponse<Void> remove(
            @PathVariable UUID templateId,
            @PathVariable UUID versionId,
            @PathVariable UUID dependencyId) {
        removeAction.execute(new RemoveProjectTemplateTaskDependencyCommand(
                templateId, versionId, dependencyId));
        return ApiResponse.success(null);
    }
}
