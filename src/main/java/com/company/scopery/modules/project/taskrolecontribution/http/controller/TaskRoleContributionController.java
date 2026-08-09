package com.company.scopery.modules.project.taskrolecontribution.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.taskrolecontribution.application.action.CreateTaskRoleContributionAction;
import com.company.scopery.modules.project.taskrolecontribution.application.action.DeleteTaskRoleContributionAction;
import com.company.scopery.modules.project.taskrolecontribution.application.command.CreateTaskRoleContributionCommand;
import com.company.scopery.modules.project.taskrolecontribution.application.command.DeleteTaskRoleContributionCommand;
import com.company.scopery.modules.project.taskrolecontribution.application.response.TaskRoleContributionResponse;
import com.company.scopery.modules.project.taskrolecontribution.application.service.TaskRoleContributionQueryService;
import com.company.scopery.modules.project.taskrolecontribution.http.request.CreateTaskRoleContributionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Project - Task Role Contributions")
@RestController
@RequestMapping(ProjectApiPaths.TASK_ROLE_CONTRIBUTIONS)
public class TaskRoleContributionController {

    private final TaskRoleContributionQueryService queryService;
    private final CreateTaskRoleContributionAction createAction;
    private final DeleteTaskRoleContributionAction deleteAction;

    public TaskRoleContributionController(TaskRoleContributionQueryService queryService,
                                          CreateTaskRoleContributionAction createAction,
                                          DeleteTaskRoleContributionAction deleteAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.deleteAction = deleteAction;
    }

    @Operation(summary = "List role contributions for a task")
    @GetMapping
    public ApiResponse<List<TaskRoleContributionResponse>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return ApiResponse.success(queryService.listByTask(taskId));
    }

    @Operation(summary = "Add a role contribution to a task")
    @PostMapping
    public ResponseEntity<ApiResponse<TaskRoleContributionResponse>> create(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody CreateTaskRoleContributionRequest request) {
        CreateTaskRoleContributionCommand cmd = new CreateTaskRoleContributionCommand(
                projectId, taskId, request.userId(),
                request.costRoleCode(), request.costRoleName(),
                request.plannedHours(), request.rateSnapshotPerHour(), request.currencyCode(),
                request.periodStart(), request.periodEnd(), request.notes());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createAction.execute(cmd)));
    }

    @Operation(summary = "Delete a role contribution")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @PathVariable UUID id) {
        deleteAction.execute(new DeleteTaskRoleContributionCommand(id, taskId, projectId));
        return ResponseEntity.noContent().build();
    }
}
