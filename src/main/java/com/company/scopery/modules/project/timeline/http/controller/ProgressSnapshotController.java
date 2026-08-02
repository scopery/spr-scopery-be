package com.company.scopery.modules.project.timeline.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.timeline.application.action.UpsertProgressSnapshotAction;
import com.company.scopery.modules.project.timeline.application.command.UpsertProgressSnapshotCommand;
import com.company.scopery.modules.project.timeline.application.response.TaskProgressSnapshotListResponse;
import com.company.scopery.modules.project.timeline.application.response.TaskProgressSnapshotResponse;
import com.company.scopery.modules.project.timeline.application.service.TimelineQueryService;
import com.company.scopery.modules.project.timeline.http.request.CreateProgressSnapshotRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Project - Timeline Progress Snapshots")
public class ProgressSnapshotController {

    private final TimelineQueryService queryService;
    private final UpsertProgressSnapshotAction upsertAction;

    public ProgressSnapshotController(
            TimelineQueryService queryService,
            UpsertProgressSnapshotAction upsertAction) {
        this.queryService = queryService;
        this.upsertAction = upsertAction;
    }

    @GetMapping(ProjectApiPaths.PROGRESS_SNAPSHOTS)
    @Operation(summary = "List progress snapshots for a project")
    public ApiResponse<TaskProgressSnapshotListResponse> listProject(
            @PathVariable UUID projectId) {
        return ApiResponse.success(queryService.listProjectSnapshots(projectId));
    }

    @GetMapping(ProjectApiPaths.TASK_PROGRESS_SNAPSHOTS)
    @Operation(summary = "List progress snapshots for a task")
    public ApiResponse<TaskProgressSnapshotListResponse> listTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return ApiResponse.success(queryService.listTaskSnapshots(projectId, taskId));
    }

    @PostMapping(ProjectApiPaths.TASK_PROGRESS_SNAPSHOTS)
    @Operation(summary = "Create or upsert a progress snapshot for a task/date")
    public ApiResponse<TaskProgressSnapshotResponse> create(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody CreateProgressSnapshotRequest request) {
        return ApiResponse.success(upsertAction.execute(new UpsertProgressSnapshotCommand(
                projectId,
                taskId,
                request.snapshotDate(),
                request.progressPercent(),
                request.timeSpentMinutes(),
                request.note())));
    }
}
