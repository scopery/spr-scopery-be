package com.company.scopery.modules.estimation.estimationrun.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.estimation.estimationrun.application.action.CancelEstimationRunAction;
import com.company.scopery.modules.estimation.estimationrun.application.action.CreateEstimationRunAction;
import com.company.scopery.modules.estimation.estimationrun.application.action.MarkCurrentEstimationRunAction;
import com.company.scopery.modules.estimation.estimationrun.application.command.CreateEstimationRunCommand;
import com.company.scopery.modules.estimation.estimationrun.application.response.EstimationRunResponse;
import com.company.scopery.modules.estimation.estimationrun.application.service.EstimationQueryService;
import com.company.scopery.modules.estimation.estimationrun.http.request.CreateEstimationRunRequest;
import com.company.scopery.modules.estimation.phaserollup.application.response.PhaseEstimateRollupResponse;
import com.company.scopery.modules.estimation.projectsummary.application.response.ProjectEstimateSummaryResponse;
import com.company.scopery.modules.estimation.shared.constant.EstimationApiPaths;
import com.company.scopery.modules.estimation.tasksnapshot.application.response.TaskEstimateSnapshotResponse;
import com.company.scopery.modules.estimation.wbsrollup.application.response.WbsEstimateRollupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(EstimationApiPaths.ESTIMATION_RUNS)
@Tag(name = "Estimation - Runs")
public class EstimationRunController {

    private final CreateEstimationRunAction create;
    private final CancelEstimationRunAction cancel;
    private final MarkCurrentEstimationRunAction markCurrent;
    private final EstimationQueryService query;

    public EstimationRunController(CreateEstimationRunAction create,
                                   CancelEstimationRunAction cancel,
                                   MarkCurrentEstimationRunAction markCurrent,
                                   EstimationQueryService query) {
        this.create = create;
        this.cancel = cancel;
        this.markCurrent = markCurrent;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create and execute an estimation run")
    public ApiResponse<EstimationRunResponse> create(@PathVariable UUID projectId,
                                                     @Valid @RequestBody CreateEstimationRunRequest request) {
        CreateEstimationRunRequest.Options opts = request.options();
        return ApiResponse.success(create.execute(new CreateEstimationRunCommand(
                projectId,
                request.name(),
                request.description(),
                request.scheduleRunId(),
                request.calculationMode(),
                request.rateTargetDateStrategy(),
                request.currencyPolicy(),
                opts != null && Boolean.TRUE.equals(opts.includeCompletedTasks()),
                opts != null && Boolean.TRUE.equals(opts.includeCancelledTasks()),
                opts != null && Boolean.TRUE.equals(opts.includeArchivedTasks()),
                opts == null || opts.useBillingPreview() == null || Boolean.TRUE.equals(opts.useBillingPreview()),
                opts == null || opts.markAsCurrent() == null || Boolean.TRUE.equals(opts.markAsCurrent())
        )));
    }

    @GetMapping
    @Operation(summary = "List estimation runs")
    public ApiResponse<List<EstimationRunResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listRuns(projectId));
    }

    @GetMapping("/{estimationRunId}")
    @Operation(summary = "Get estimation run")
    public ApiResponse<EstimationRunResponse> get(@PathVariable UUID projectId,
                                                  @PathVariable UUID estimationRunId) {
        return ApiResponse.success(query.getRun(projectId, estimationRunId));
    }

    @PostMapping("/{estimationRunId}/cancel")
    @Operation(summary = "Cancel pending or running estimation run")
    public ApiResponse<EstimationRunResponse> cancel(@PathVariable UUID projectId,
                                                     @PathVariable UUID estimationRunId) {
        return ApiResponse.success(cancel.execute(projectId, estimationRunId));
    }

    @PostMapping("/{estimationRunId}/mark-current")
    @Operation(summary = "Mark estimation run as current")
    public ApiResponse<EstimationRunResponse> markCurrent(@PathVariable UUID projectId,
                                                          @PathVariable UUID estimationRunId) {
        return ApiResponse.success(markCurrent.execute(projectId, estimationRunId));
    }

    @GetMapping("/{estimationRunId}/tasks")
    @Operation(summary = "List task estimate snapshots for a run")
    public ApiResponse<List<TaskEstimateSnapshotResponse>> tasks(@PathVariable UUID projectId,
                                                                 @PathVariable UUID estimationRunId) {
        return ApiResponse.success(query.listTasks(projectId, estimationRunId));
    }

    @GetMapping("/{estimationRunId}/wbs-rollups")
    @Operation(summary = "List WBS estimate roll-ups for a run")
    public ApiResponse<List<WbsEstimateRollupResponse>> wbs(@PathVariable UUID projectId,
                                                            @PathVariable UUID estimationRunId) {
        return ApiResponse.success(query.listWbs(projectId, estimationRunId));
    }

    @GetMapping("/{estimationRunId}/phase-rollups")
    @Operation(summary = "List phase estimate roll-ups for a run")
    public ApiResponse<List<PhaseEstimateRollupResponse>> phases(@PathVariable UUID projectId,
                                                                 @PathVariable UUID estimationRunId) {
        return ApiResponse.success(query.listPhases(projectId, estimationRunId));
    }

    @GetMapping("/{estimationRunId}/summary")
    @Operation(summary = "Get project estimate summary for a run")
    public ApiResponse<ProjectEstimateSummaryResponse> summary(@PathVariable UUID projectId,
                                                               @PathVariable UUID estimationRunId) {
        return ApiResponse.success(query.getSummary(projectId, estimationRunId));
    }
}
