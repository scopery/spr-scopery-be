package com.company.scopery.modules.project.taskdependency.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.taskdependency.application.action.CreateTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.action.RemoveTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.command.CreateTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.command.RemoveTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.query.SearchTaskDependencyQuery;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import com.company.scopery.modules.project.taskdependency.application.service.TaskDependencyQueryService;
import com.company.scopery.modules.project.taskdependency.http.request.CreateTaskDependencyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Project - Task Dependencies")
@RestController
@RequestMapping(ProjectApiPaths.TASK_DEPENDENCIES)
public class TaskDependencyController {

    private final TaskDependencyQueryService queryService;
    private final CreateTaskDependencyAction createTaskDependencyAction;
    private final RemoveTaskDependencyAction removeTaskDependencyAction;

    public TaskDependencyController(TaskDependencyQueryService queryService,
                                    CreateTaskDependencyAction createTaskDependencyAction,
                                    RemoveTaskDependencyAction removeTaskDependencyAction) {
        this.queryService = queryService;
        this.createTaskDependencyAction = createTaskDependencyAction;
        this.removeTaskDependencyAction = removeTaskDependencyAction;
    }

    @Operation(summary = "Create a task dependency")
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDependencyResponse>> createTaskDependency(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskDependencyRequest request) {
        CreateTaskDependencyCommand command = new CreateTaskDependencyCommand(
                projectId,
                request.predecessorTaskId(),
                request.successorTaskId(),
                request.dependencyType(),
                request.lagDays());
        return ResponseEntity.ok(ApiResponse.success(createTaskDependencyAction.execute(command)));
    }

    @Operation(summary = "Get a task dependency by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDependencyResponse>> getTaskDependency(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getTaskDependency(projectId, id)));
    }

    @Operation(summary = "Search task dependencies")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskDependencyResponse>>> searchTaskDependencies(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID taskId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchTaskDependencyQuery query = new SearchTaskDependencyQuery(projectId, taskId, status, page, size);
        PageResult<TaskDependencyResponse> result = queryService.searchTaskDependencies(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Remove a task dependency")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeTaskDependency(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        removeTaskDependencyAction.execute(new RemoveTaskDependencyCommand(id, projectId));
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
