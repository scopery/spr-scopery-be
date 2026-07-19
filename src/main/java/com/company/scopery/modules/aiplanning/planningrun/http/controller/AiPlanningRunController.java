package com.company.scopery.modules.aiplanning.planningrun.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiplanning.planningrun.application.action.CancelAiPlanningRunAction;
import com.company.scopery.modules.aiplanning.planningrun.application.action.CreateAiPlanningRunAction;
import com.company.scopery.modules.aiplanning.planningrun.application.command.CancelAiPlanningRunCommand;
import com.company.scopery.modules.aiplanning.planningrun.application.command.CreateAiPlanningRunCommand;
import com.company.scopery.modules.aiplanning.planningrun.application.response.AiPlanningRunResponse;
import com.company.scopery.modules.aiplanning.planningrun.application.service.AiPlanningRunQueryService;
import com.company.scopery.modules.aiplanning.planningrun.http.request.CreateAiPlanningRunRequest;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningApiPaths;
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
@RequestMapping(AiPlanningApiPaths.RUNS)
@Tag(name = "AI Planning - Runs")
public class AiPlanningRunController {
    private final CreateAiPlanningRunAction create;
    private final CancelAiPlanningRunAction cancel;
    private final AiPlanningRunQueryService query;

    public AiPlanningRunController(CreateAiPlanningRunAction create, CancelAiPlanningRunAction cancel,
                                   AiPlanningRunQueryService query) {
        this.create = create; this.cancel = cancel; this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create AI planning run (proposal generation)")
    public ApiResponse<AiPlanningRunResponse> create(@PathVariable UUID projectId,
                                                     @Valid @RequestBody CreateAiPlanningRunRequest request) {
        return ApiResponse.success(create.execute(new CreateAiPlanningRunCommand(
                projectId, request.runType(), request.agentId(), request.promptTemplateCode(),
                request.input(), request.includeSections(), request.options())));
    }

    @GetMapping
    @Operation(summary = "List AI planning runs")
    public ApiResponse<List<AiPlanningRunResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{runId}")
    @Operation(summary = "Get AI planning run")
    public ApiResponse<AiPlanningRunResponse> get(@PathVariable UUID projectId, @PathVariable UUID runId) {
        return ApiResponse.success(query.get(projectId, runId));
    }

    @PostMapping("/{runId}/cancel")
    @Operation(summary = "Cancel AI planning run")
    public ApiResponse<AiPlanningRunResponse> cancel(@PathVariable UUID projectId, @PathVariable UUID runId) {
        return ApiResponse.success(cancel.execute(new CancelAiPlanningRunCommand(projectId, runId)));
    }
}
