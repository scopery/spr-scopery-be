package com.company.scopery.modules.projectnotification.preference.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectnotification.preference.application.action.UpsertProjectPreferencesAction;
import com.company.scopery.modules.projectnotification.preference.application.command.UpsertProjectPreferencesCommand;
import com.company.scopery.modules.projectnotification.preference.application.response.ProjectNotificationPreferenceResponse;
import com.company.scopery.modules.projectnotification.preference.application.service.ProjectPreferenceQueryService;
import com.company.scopery.modules.projectnotification.preference.http.request.UpsertProjectPreferencesRequest;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Project Notification - Preferences")
public class ProjectNotificationPreferenceController {
    private final UpsertProjectPreferencesAction upsertAction;
    private final ProjectPreferenceQueryService queryService;

    public ProjectNotificationPreferenceController(
            UpsertProjectPreferencesAction upsertAction,
            ProjectPreferenceQueryService queryService) {
        this.upsertAction = upsertAction;
        this.queryService = queryService;
    }

    @GetMapping(ProjectNotificationApiPaths.PROJECT_PREFERENCES)
    @Operation(summary = "Get my project notification preferences")
    public ApiResponse<List<ProjectNotificationPreferenceResponse>> getMine(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.listMine(projectId));
    }

    @PutMapping(ProjectNotificationApiPaths.PROJECT_PREFERENCES)
    @Operation(summary = "Upsert my project notification preferences")
    public ApiResponse<List<ProjectNotificationPreferenceResponse>> upsert(
            @PathVariable UUID projectId,
            @Valid @RequestBody UpsertProjectPreferencesRequest request) {
        var items = request.preferences().stream()
                .map(p -> new UpsertProjectPreferencesCommand.PreferenceItem(
                        p.eventCode(), p.channel(), p.enabled(), p.muted()))
                .toList();
        return ApiResponse.success(upsertAction.execute(
                new UpsertProjectPreferencesCommand(projectId, null, items)));
    }

    @GetMapping(ProjectNotificationApiPaths.TASK_PREFERENCES)
    @Operation(summary = "Get my task notification preferences")
    public ApiResponse<List<ProjectNotificationPreferenceResponse>> getMineTask(
            @PathVariable UUID projectId, @PathVariable UUID taskId) {
        return ApiResponse.success(queryService.listMineForTask(projectId, taskId));
    }

    @PutMapping(ProjectNotificationApiPaths.TASK_PREFERENCES)
    @Operation(summary = "Upsert my task notification preferences")
    public ApiResponse<List<ProjectNotificationPreferenceResponse>> upsertTask(
            @PathVariable UUID projectId, @PathVariable UUID taskId,
            @Valid @RequestBody UpsertProjectPreferencesRequest request) {
        var items = request.preferences().stream()
                .map(p -> new UpsertProjectPreferencesCommand.PreferenceItem(
                        p.eventCode(), p.channel(), p.enabled(), p.muted()))
                .toList();
        return ApiResponse.success(upsertAction.execute(
                new UpsertProjectPreferencesCommand(projectId, taskId, items)));
    }
}
