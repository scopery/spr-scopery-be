package com.company.scopery.modules.projectnotification.reminder.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectnotification.reminder.application.action.RunProjectReminderAction;
import com.company.scopery.modules.projectnotification.reminder.application.response.ProjectReminderRunResponse;
import com.company.scopery.modules.projectnotification.reminder.application.service.ProjectReminderQueryService;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectNotificationApiPaths.REMINDERS)
@Tag(name = "Project Notification - Reminders")
public class ProjectReminderController {
    private final RunProjectReminderAction runAction;
    private final ProjectReminderQueryService queryService;

    public ProjectReminderController(RunProjectReminderAction runAction,
                                     ProjectReminderQueryService queryService) {
        this.runAction = runAction;
        this.queryService = queryService;
    }

    @PostMapping("/run")
    @Operation(summary = "Manually run due-date reminder job")
    public ApiResponse<ProjectReminderRunResponse> run(@RequestParam UUID workspaceId) {
        return ApiResponse.success(runAction.execute(workspaceId));
    }

    @GetMapping("/runs")
    @Operation(summary = "List recent reminder runs")
    public ApiResponse<List<ProjectReminderRunResponse>> list(@RequestParam UUID workspaceId) {
        return ApiResponse.success(queryService.list(workspaceId));
    }

    @GetMapping("/runs/{runId}")
    @Operation(summary = "Get reminder run by id")
    public ApiResponse<ProjectReminderRunResponse> get(
            @RequestParam UUID workspaceId, @PathVariable UUID runId) {
        return ApiResponse.success(queryService.get(workspaceId, runId));
    }
}
