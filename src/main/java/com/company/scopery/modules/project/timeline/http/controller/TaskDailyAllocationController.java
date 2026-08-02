package com.company.scopery.modules.project.timeline.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.timeline.application.action.ReplaceDailyAllocationsAction;
import com.company.scopery.modules.project.timeline.application.command.ReplaceDailyAllocationsCommand;
import com.company.scopery.modules.project.timeline.application.response.TaskDailyAllocationListResponse;
import com.company.scopery.modules.project.timeline.application.service.TimelineQueryService;
import com.company.scopery.modules.project.timeline.http.request.ReplaceDailyAllocationsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Project - Timeline Daily Allocations")
public class TaskDailyAllocationController {

    private final TimelineQueryService queryService;
    private final ReplaceDailyAllocationsAction replaceAction;

    public TaskDailyAllocationController(
            TimelineQueryService queryService,
            ReplaceDailyAllocationsAction replaceAction) {
        this.queryService = queryService;
        this.replaceAction = replaceAction;
    }

    @GetMapping(ProjectApiPaths.DAILY_ALLOCATIONS)
    @Operation(summary = "List daily allocations for a project")
    public ApiResponse<TaskDailyAllocationListResponse> listProject(
            @PathVariable UUID projectId) {
        return ApiResponse.success(queryService.listProjectAllocations(projectId));
    }

    @GetMapping(ProjectApiPaths.TASK_DAILY_ALLOCATIONS)
    @Operation(summary = "List daily allocations for a task")
    public ApiResponse<TaskDailyAllocationListResponse> list(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return ApiResponse.success(queryService.listTaskAllocations(projectId, taskId));
    }

    @PutMapping(ProjectApiPaths.TASK_DAILY_ALLOCATIONS)
    @Operation(summary = "Replace MANUAL daily allocations for a task")
    public ApiResponse<TaskDailyAllocationListResponse> replace(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody ReplaceDailyAllocationsRequest request) {
        return ApiResponse.success(replaceAction.execute(new ReplaceDailyAllocationsCommand(
                projectId,
                taskId,
                request.items().stream()
                        .map(i -> new ReplaceDailyAllocationsCommand.DailyAllocationItem(
                                i.workDate(), i.plannedMinutes()))
                        .toList())));
    }
}
