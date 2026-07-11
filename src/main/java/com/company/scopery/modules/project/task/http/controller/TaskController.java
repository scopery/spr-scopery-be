package com.company.scopery.modules.project.task.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.task.application.action.*;
import com.company.scopery.modules.project.task.application.command.*;
import com.company.scopery.modules.project.task.application.query.SearchTaskQuery;
import com.company.scopery.modules.project.task.application.response.TaskResponse;
import com.company.scopery.modules.project.task.application.service.TaskQueryService;
import com.company.scopery.modules.project.task.http.request.CreateTaskRequest;
import com.company.scopery.modules.project.task.http.request.UpdateTaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.TASKS)
@Tag(name = "Project - Tasks")
public class TaskController {

    private final TaskQueryService taskQueryService;
    private final CreateTaskAction createTaskAction;
    private final UpdateTaskAction updateTaskAction;
    private final StartTaskAction startTaskAction;
    private final BlockTaskAction blockTaskAction;
    private final CompleteTaskAction completeTaskAction;
    private final CancelTaskAction cancelTaskAction;
    private final ArchiveTaskAction archiveTaskAction;

    public TaskController(TaskQueryService taskQueryService,
                          CreateTaskAction createTaskAction,
                          UpdateTaskAction updateTaskAction,
                          StartTaskAction startTaskAction,
                          BlockTaskAction blockTaskAction,
                          CompleteTaskAction completeTaskAction,
                          CancelTaskAction cancelTaskAction,
                          ArchiveTaskAction archiveTaskAction) {
        this.taskQueryService = taskQueryService;
        this.createTaskAction = createTaskAction;
        this.updateTaskAction = updateTaskAction;
        this.startTaskAction = startTaskAction;
        this.blockTaskAction = blockTaskAction;
        this.completeTaskAction = completeTaskAction;
        this.cancelTaskAction = cancelTaskAction;
        this.archiveTaskAction = archiveTaskAction;
    }

    @PostMapping
    @Operation(summary = "Create a task")
    public ApiResponse<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        CreateTaskCommand cmd = new CreateTaskCommand(
                projectId,
                request.projectPhaseId(),
                request.wbsNodeId(),
                request.code(),
                request.title(),
                request.description(),
                request.inChargeUserId(),
                request.plannedRoleCode(),
                request.plannedRoleName(),
                request.estimateHours(),
                request.plannedStartDate(),
                request.dueDate(),
                request.priority()
        );
        return ApiResponse.success(createTaskAction.execute(cmd));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ApiResponse<TaskResponse> getTask(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(taskQueryService.getTask(projectId, id));
    }

    @GetMapping
    @Operation(summary = "Search tasks")
    public ApiResponse<PageResponse<TaskResponse>> searchTasks(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID projectPhaseId,
            @RequestParam(required = false) UUID wbsNodeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchTaskQuery query = new SearchTaskQuery(
                projectId, projectPhaseId, wbsNodeId, status, priority, keyword, page, size);
        PageResult<TaskResponse> result = taskQueryService.searchTasks(query);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        UpdateTaskCommand cmd = new UpdateTaskCommand(
                id,
                projectId,
                request.title(),
                request.description(),
                request.inChargeUserId(),
                request.plannedRoleCode(),
                request.plannedRoleName(),
                request.estimateHours(),
                request.plannedStartDate(),
                request.dueDate(),
                request.priority()
        );
        return ApiResponse.success(updateTaskAction.execute(cmd));
    }

    @PatchMapping("/{id}/start")
    @Operation(summary = "Start a task")
    public ApiResponse<TaskResponse> startTask(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(startTaskAction.execute(new StartTaskCommand(id, projectId)));
    }

    @PatchMapping("/{id}/block")
    @Operation(summary = "Block a task")
    public ApiResponse<TaskResponse> blockTask(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(blockTaskAction.execute(new BlockTaskCommand(id, projectId)));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete a task")
    public ApiResponse<TaskResponse> completeTask(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(completeTaskAction.execute(new CompleteTaskCommand(id, projectId)));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a task")
    public ApiResponse<TaskResponse> cancelTask(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(cancelTaskAction.execute(new CancelTaskCommand(id, projectId)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a task")
    public ApiResponse<TaskResponse> archiveTask(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(archiveTaskAction.execute(new ArchiveTaskCommand(id, projectId)));
    }
}
