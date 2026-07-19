package com.company.scopery.modules.project.gantt.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.gantt.application.action.*;
import com.company.scopery.modules.project.gantt.application.command.*;
import com.company.scopery.modules.project.gantt.application.query.GanttViewQuery;
import com.company.scopery.modules.project.gantt.application.response.*;
import com.company.scopery.modules.project.gantt.application.service.GanttQueryService;
import com.company.scopery.modules.project.gantt.http.request.*;
import com.company.scopery.modules.project.scheduleoverride.application.response.TaskScheduleOverrideResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.GANTT)
@Tag(name = "Project - Gantt")
public class GanttController {

    private final GanttQueryService queryService;
    private final RecalculateGanttAction recalculateGanttAction;
    private final MoveGanttTaskAction moveGanttTaskAction;
    private final ResizeGanttTaskAction resizeGanttTaskAction;
    private final ClearGanttTaskOverrideAction clearGanttTaskOverrideAction;
    private final CreateGanttDependencyAction createGanttDependencyAction;
    private final RemoveGanttDependencyAction removeGanttDependencyAction;

    public GanttController(GanttQueryService queryService,
                           RecalculateGanttAction recalculateGanttAction,
                           MoveGanttTaskAction moveGanttTaskAction,
                           ResizeGanttTaskAction resizeGanttTaskAction,
                           ClearGanttTaskOverrideAction clearGanttTaskOverrideAction,
                           CreateGanttDependencyAction createGanttDependencyAction,
                           RemoveGanttDependencyAction removeGanttDependencyAction) {
        this.queryService = queryService;
        this.recalculateGanttAction = recalculateGanttAction;
        this.moveGanttTaskAction = moveGanttTaskAction;
        this.resizeGanttTaskAction = resizeGanttTaskAction;
        this.clearGanttTaskOverrideAction = clearGanttTaskOverrideAction;
        this.createGanttDependencyAction = createGanttDependencyAction;
        this.removeGanttDependencyAction = removeGanttDependencyAction;
    }

    @GetMapping
    @Operation(summary = "Get full Gantt projection")
    public ApiResponse<GanttViewResponse> getGantt(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID scheduleRunId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "true") boolean includeUnscheduled,
            @RequestParam(defaultValue = "false") boolean includeArchived,
            @RequestParam(defaultValue = "PHASE") String groupBy) {
        return ApiResponse.success(queryService.getView(new GanttViewQuery(
                projectId, scheduleRunId, dateFrom, dateTo, includeUnscheduled, includeArchived, groupBy)));
    }

    @GetMapping("/items")
    @Operation(summary = "Get Gantt items slice")
    public ApiResponse<List<GanttItemResponse>> getItems(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID scheduleRunId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "true") boolean includeUnscheduled,
            @RequestParam(defaultValue = "false") boolean includeArchived,
            @RequestParam(defaultValue = "PHASE") String groupBy) {
        return ApiResponse.success(queryService.getView(new GanttViewQuery(
                projectId, scheduleRunId, dateFrom, dateTo, includeUnscheduled, includeArchived, groupBy)).items());
    }

    @GetMapping("/dependencies")
    @Operation(summary = "Get Gantt dependencies slice")
    public ApiResponse<List<GanttDependencyResponse>> getDependencies(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID scheduleRunId) {
        return ApiResponse.success(queryService.getView(new GanttViewQuery(
                projectId, scheduleRunId, null, null, true, false, "PHASE")).dependencies());
    }

    @GetMapping("/issues")
    @Operation(summary = "Get Gantt issues slice")
    public ApiResponse<List<GanttIssueResponse>> getIssues(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID scheduleRunId) {
        return ApiResponse.success(queryService.getView(new GanttViewQuery(
                projectId, scheduleRunId, null, null, true, false, "PHASE")).issues());
    }

    @GetMapping("/critical-path")
    @Operation(summary = "Compute critical path (CPM) from task dependencies and scheduled dates")
    public ApiResponse<GanttCriticalPathResponse> criticalPath(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID scheduleRunId) {
        return ApiResponse.success(queryService.getCriticalPath(projectId, scheduleRunId));
    }

    @GetMapping("/export")
    @Operation(summary = "Export Gantt task rows as CSV or JSON")
    public ResponseEntity<?> exportGantt(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) UUID scheduleRunId) {
        GanttExportResponse export = queryService.exportTasks(projectId, scheduleRunId, format);
        if ("CSV".equals(export.format())) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"gantt-tasks.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(queryService.exportTasksCsv(export));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.success(export));
    }

    @PostMapping("/recalculate")
    @Operation(summary = "Recalculate project schedule via Gantt")
    public ApiResponse<GanttRecalculateResponse> recalculate(
            @PathVariable UUID projectId,
            @Valid @RequestBody RecalculateGanttRequest request) {
        return ApiResponse.success(recalculateGanttAction.execute(new RecalculateGanttCommand(
                projectId,
                request.planningStartDate(),
                request.planningEndDate(),
                Boolean.TRUE.equals(request.includeCompletedTasks()),
                request.markAsCurrent() == null || request.markAsCurrent())));
    }

    @PostMapping("/tasks/{taskId}/move")
    @Operation(summary = "Move Gantt task bar (creates schedule override)")
    public ApiResponse<TaskScheduleOverrideResponse> move(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody MoveGanttTaskRequest request) {
        return ApiResponse.success(moveGanttTaskAction.execute(new MoveGanttTaskCommand(
                projectId, taskId, request.manualStartDate(), request.manualFinishDate(),
                request.reason(), request.recalculate() == null || request.recalculate())));
    }

    @PostMapping("/tasks/{taskId}/resize")
    @Operation(summary = "Resize Gantt task bar (does not change estimateHours)")
    public ApiResponse<TaskScheduleOverrideResponse> resize(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody ResizeGanttTaskRequest request) {
        return ApiResponse.success(resizeGanttTaskAction.execute(new ResizeGanttTaskCommand(
                projectId, taskId, request.manualFinishDate(),
                request.reason(), request.recalculate() == null || request.recalculate())));
    }

    @PostMapping("/tasks/{taskId}/clear-override")
    @Operation(summary = "Clear active schedule override for a task")
    public ApiResponse<TaskScheduleOverrideResponse> clearOverride(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestParam(defaultValue = "true") boolean recalculate) {
        return ApiResponse.success(clearGanttTaskOverrideAction.execute(
                new ClearGanttTaskOverrideCommand(projectId, taskId, recalculate)));
    }

    @PostMapping("/dependencies")
    @Operation(summary = "Create dependency from Gantt")
    public ApiResponse<TaskDependencyResponse> createDependency(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateGanttDependencyRequest request) {
        return ApiResponse.success(createGanttDependencyAction.execute(new CreateGanttDependencyCommand(
                projectId,
                request.predecessorTaskId(),
                request.successorTaskId(),
                request.dependencyType() != null ? request.dependencyType() : "FINISH_TO_START",
                request.lagDays() != null ? request.lagDays() : 0,
                request.recalculate() == null || request.recalculate())));
    }

    @DeleteMapping("/dependencies/{dependencyId}")
    @Operation(summary = "Remove dependency from Gantt")
    public ApiResponse<TaskDependencyResponse> removeDependency(
            @PathVariable UUID projectId,
            @PathVariable UUID dependencyId,
            @RequestParam(defaultValue = "true") boolean recalculate) {
        return ApiResponse.success(removeGanttDependencyAction.execute(
                new RemoveGanttDependencyCommand(projectId, dependencyId, recalculate)));
    }
}
