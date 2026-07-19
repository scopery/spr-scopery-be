package com.company.scopery.modules.projectbaseline.baseline.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectbaseline.baseline.application.action.*;
import com.company.scopery.modules.projectbaseline.baseline.application.command.CreateBaselineCommand;
import com.company.scopery.modules.projectbaseline.baseline.application.command.UpdateBaselineCommand;
import com.company.scopery.modules.projectbaseline.baseline.application.response.ProjectBaselineResponse;
import com.company.scopery.modules.projectbaseline.baseline.application.service.BaselineQueryService;
import com.company.scopery.modules.projectbaseline.baseline.http.request.CreateBaselineRequest;
import com.company.scopery.modules.projectbaseline.baseline.http.request.UpdateBaselineRequest;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Project Baseline")
public class BaselineController {

    private final BaselineQueryService queryService;
    private final CreateBaselineAction createAction;
    private final UpdateBaselineAction updateAction;
    private final RefreshBaselineSnapshotAction refreshAction;
    private final ValidateBaselineAction validateAction;
    private final ApproveBaselineAction approveAction;
    private final MarkCurrentBaselineAction markCurrentAction;
    private final ArchiveBaselineAction archiveAction;

    public BaselineController(BaselineQueryService queryService, CreateBaselineAction createAction,
                              UpdateBaselineAction updateAction, RefreshBaselineSnapshotAction refreshAction,
                              ValidateBaselineAction validateAction, ApproveBaselineAction approveAction,
                              MarkCurrentBaselineAction markCurrentAction, ArchiveBaselineAction archiveAction) {
        this.queryService = queryService; this.createAction = createAction; this.updateAction = updateAction;
        this.refreshAction = refreshAction; this.validateAction = validateAction; this.approveAction = approveAction;
        this.markCurrentAction = markCurrentAction; this.archiveAction = archiveAction;
    }

    @PostMapping(ProjectBaselineApiPaths.BASELINES)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create draft baseline")
    public ApiResponse<ProjectBaselineResponse> create(@PathVariable UUID projectId,
                                                       @Valid @RequestBody CreateBaselineRequest request) {
        return ApiResponse.success(createAction.execute(new CreateBaselineCommand(
                projectId, request.name(), request.description(),
                request.sourceScheduleRunId(), request.sourceEstimationRunId(),
                request.sourceFinanceScenarioId(), request.sourceQuoteVersionId())));
    }

    @GetMapping(ProjectBaselineApiPaths.BASELINES)
    @Operation(summary = "List baselines")
    public ApiResponse<List<ProjectBaselineResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.list(projectId));
    }

    @GetMapping(ProjectBaselineApiPaths.CURRENT_BASELINE)
    @Operation(summary = "Get current baseline")
    public ApiResponse<ProjectBaselineResponse> current(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.getCurrent(projectId));
    }

    @GetMapping(ProjectBaselineApiPaths.BASELINES + "/{baselineId}")
    @Operation(summary = "Get baseline")
    public ApiResponse<ProjectBaselineResponse> get(@PathVariable UUID projectId, @PathVariable UUID baselineId) {
        return ApiResponse.success(queryService.get(projectId, baselineId));
    }

    @PutMapping(ProjectBaselineApiPaths.BASELINES + "/{baselineId}")
    @Operation(summary = "Update draft baseline")
    public ApiResponse<ProjectBaselineResponse> update(@PathVariable UUID projectId, @PathVariable UUID baselineId,
                                                       @Valid @RequestBody UpdateBaselineRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateBaselineCommand(
                projectId, baselineId, request.name(), request.description())));
    }

    @PostMapping(ProjectBaselineApiPaths.BASELINES + "/{baselineId}/refresh-snapshot")
    @Operation(summary = "Refresh baseline snapshot")
    public ApiResponse<ProjectBaselineResponse> refresh(@PathVariable UUID projectId, @PathVariable UUID baselineId) {
        return ApiResponse.success(refreshAction.execute(projectId, baselineId));
    }

    @PostMapping(ProjectBaselineApiPaths.BASELINES + "/{baselineId}/validate")
    @Operation(summary = "Validate baseline")
    public ApiResponse<ProjectBaselineResponse> validate(@PathVariable UUID projectId, @PathVariable UUID baselineId) {
        return ApiResponse.success(validateAction.execute(projectId, baselineId));
    }

    @PostMapping(ProjectBaselineApiPaths.BASELINES + "/{baselineId}/approve")
    @Operation(summary = "Approve baseline")
    public ApiResponse<ProjectBaselineResponse> approve(@PathVariable UUID projectId, @PathVariable UUID baselineId) {
        return ApiResponse.success(approveAction.execute(projectId, baselineId));
    }

    @PostMapping(ProjectBaselineApiPaths.BASELINES + "/{baselineId}/mark-current")
    @Operation(summary = "Mark baseline current")
    public ApiResponse<ProjectBaselineResponse> markCurrent(@PathVariable UUID projectId, @PathVariable UUID baselineId) {
        return ApiResponse.success(markCurrentAction.execute(projectId, baselineId));
    }

    @PatchMapping(ProjectBaselineApiPaths.BASELINES + "/{baselineId}/archive")
    @Operation(summary = "Archive baseline")
    public ApiResponse<ProjectBaselineResponse> archive(@PathVariable UUID projectId, @PathVariable UUID baselineId) {
        return ApiResponse.success(archiveAction.execute(projectId, baselineId));
    }
}
