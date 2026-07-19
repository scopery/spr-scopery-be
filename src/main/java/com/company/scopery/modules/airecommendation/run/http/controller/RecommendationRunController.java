package com.company.scopery.modules.airecommendation.run.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.airecommendation.application.action.CreateRecommendationRunAction;
import com.company.scopery.modules.airecommendation.application.command.CreateRecommendationRunCommand;
import com.company.scopery.modules.airecommendation.application.response.CreateRunResponse;
import com.company.scopery.modules.airecommendation.application.response.RecommendationRunResponse;
import com.company.scopery.modules.airecommendation.application.service.RecommendationRunQueryService;
import com.company.scopery.modules.airecommendation.domain.enums.TriggerType;
import com.company.scopery.modules.airecommendation.run.http.request.CreateRecommendationRunRequest;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "AI Recommendations - Runs")
public class RecommendationRunController {

    private final CreateRecommendationRunAction createRunAction;
    private final RecommendationRunQueryService queryService;

    public RecommendationRunController(CreateRecommendationRunAction createRunAction,
                                        RecommendationRunQueryService queryService) {
        this.createRunAction = createRunAction;
        this.queryService = queryService;
    }

    @PostMapping(AiRecommendationApiPaths.PROJECT_RUNS)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Start a recommendation run for a project")
    public ApiResponse<CreateRunResponse> createRun(
            @PathVariable UUID projectId,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @Valid @RequestBody CreateRecommendationRunRequest request) {

        TriggerType triggerType = parseTriggerType(request.triggerType());
        CreateRecommendationRunCommand cmd = new CreateRecommendationRunCommand(
                workspaceId, projectId, actorId,
                request.policyCode(),
                request.packCodes() != null ? request.packCodes() : java.util.List.of(),
                triggerType,
                request.idempotencyKey(),
                null, null, null,
                MDC.get("traceId")
        );
        return ApiResponse.success(createRunAction.execute(cmd));
    }

    @GetMapping(AiRecommendationApiPaths.RUN_BY_ID)
    @Operation(summary = "Get recommendation run by ID")
    public ApiResponse<RecommendationRunResponse> getRunById(
            @PathVariable UUID runId,
            @RequestParam UUID workspaceId) {
        return ApiResponse.success(queryService.getRunById(runId, workspaceId));
    }

    private TriggerType parseTriggerType(String raw) {
        if (raw == null || raw.isBlank()) return TriggerType.MANUAL;
        try {
            return TriggerType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TriggerType.MANUAL;
        }
    }
}
