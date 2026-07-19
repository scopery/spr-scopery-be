package com.company.scopery.modules.airecommendation.nextbestaction.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.airecommendation.application.query.GetNextBestActionsQuery;
import com.company.scopery.modules.airecommendation.application.response.NextBestActionItemResponse;
import com.company.scopery.modules.airecommendation.application.service.NextBestActionQueryService;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "AI Recommendations - Next Best Actions")
public class NextBestActionController {

    private final NextBestActionQueryService queryService;

    public NextBestActionController(NextBestActionQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping(AiRecommendationApiPaths.PROJECT_NEXT_BEST_ACTIONS)
    @Operation(summary = "List next best actions for a project or suggestion")
    public ApiResponse<List<NextBestActionItemResponse>> listNextBestActions(
            @PathVariable UUID projectId,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) String suggestionRef,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(defaultValue = "10") int limit) {

        GetNextBestActionsQuery query = new GetNextBestActionsQuery(
                workspaceId, projectId, actorId, suggestionRef, entityType, entityId, limit);
        return ApiResponse.success(queryService.listNextBestActions(query));
    }
}
