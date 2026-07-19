package com.company.scopery.modules.project.scheduling.schedulerun.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.scheduling.scheduleddailywork.application.response.ScheduledDailyWorkResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.application.service.ScheduleQueryService;
import com.company.scopery.modules.project.scheduling.schedulingissue.application.response.SchedulingIssueResponse;
import com.company.scopery.modules.project.scheduling.taskschedule.application.response.TaskScheduleResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.CURRENT_SCHEDULE)
@Tag(name = "Project - Current Schedule")
public class ProjectScheduleController {

    private final ScheduleQueryService query;

    public ProjectScheduleController(ScheduleQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "Get current schedule run")
    public ApiResponse<ScheduleRunResponse> current(@PathVariable UUID projectId) {
        return ApiResponse.success(query.current(projectId));
    }

    @GetMapping("/tasks")
    @Operation(summary = "List current task schedules")
    public ApiResponse<List<TaskScheduleResponse>> tasks(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID taskId,
            @RequestParam(required = false) UUID assigneeUserId,
            @RequestParam(required = false) String riskStatus,
            @RequestParam(required = false) String scheduleStatus) {
        return ApiResponse.success(query.currentTasks(projectId, taskId, assigneeUserId, riskStatus, scheduleStatus));
    }

    @GetMapping("/daily-work")
    @Operation(summary = "List current daily scheduled work")
    public ApiResponse<List<ScheduledDailyWorkResponse>> work(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID taskId,
            @RequestParam(required = false) UUID assigneeUserId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ApiResponse.success(query.currentWork(projectId, taskId, assigneeUserId, dateFrom, dateTo));
    }

    @GetMapping("/issues")
    @Operation(summary = "List current scheduling issues")
    public ApiResponse<List<SchedulingIssueResponse>> issues(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID taskId,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String severity) {
        return ApiResponse.success(query.currentIssues(projectId, taskId, issueType, severity));
    }
}
