package com.company.scopery.modules.estimation.estimationrun.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.estimation.estimationrun.application.response.EstimationRunResponse;
import com.company.scopery.modules.estimation.estimationrun.application.service.EstimationQueryService;
import com.company.scopery.modules.estimation.phaserollup.application.response.PhaseEstimateRollupResponse;
import com.company.scopery.modules.estimation.projectsummary.application.response.ProjectEstimateSummaryResponse;
import com.company.scopery.modules.estimation.shared.constant.EstimationApiPaths;
import com.company.scopery.modules.estimation.tasksnapshot.application.response.TaskEstimateSnapshotResponse;
import com.company.scopery.modules.estimation.wbsrollup.application.response.WbsEstimateRollupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(EstimationApiPaths.CURRENT_ESTIMATION)
@Tag(name = "Estimation - Current")
public class CurrentEstimationController {

    private final EstimationQueryService query;

    public CurrentEstimationController(EstimationQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "Get current estimation run")
    public ApiResponse<EstimationRunResponse> current(@PathVariable UUID projectId) {
        return ApiResponse.success(query.getCurrent(projectId));
    }

    @GetMapping("/tasks")
    @Operation(summary = "List current task estimate snapshots")
    public ApiResponse<List<TaskEstimateSnapshotResponse>> tasks(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listCurrentTasks(projectId));
    }

    @GetMapping("/wbs-rollups")
    @Operation(summary = "List current WBS estimate roll-ups")
    public ApiResponse<List<WbsEstimateRollupResponse>> wbs(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listCurrentWbs(projectId));
    }

    @GetMapping("/phase-rollups")
    @Operation(summary = "List current phase estimate roll-ups")
    public ApiResponse<List<PhaseEstimateRollupResponse>> phases(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listCurrentPhases(projectId));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get current project estimate summary")
    public ApiResponse<ProjectEstimateSummaryResponse> summary(@PathVariable UUID projectId) {
        return ApiResponse.success(query.getCurrentSummary(projectId));
    }
}
