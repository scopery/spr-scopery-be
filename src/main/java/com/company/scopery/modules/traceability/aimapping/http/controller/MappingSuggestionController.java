package com.company.scopery.modules.traceability.aimapping.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.aimapping.application.action.ApplyMappingDraftAction;
import com.company.scopery.modules.traceability.aimapping.application.action.GenerateMappingSuggestionsAction;
import com.company.scopery.modules.traceability.aimapping.application.action.ReviewMappingSuggestionsAction;
import com.company.scopery.modules.traceability.aimapping.application.command.ApplyMappingDraftCommand;
import com.company.scopery.modules.traceability.aimapping.application.command.GenerateMappingCommand;
import com.company.scopery.modules.traceability.aimapping.application.command.ReviewSuggestionsCommand;
import com.company.scopery.modules.traceability.aimapping.application.query.ListMappingSuggestionsQuery;
import com.company.scopery.modules.traceability.aimapping.application.response.ApplyMappingDraftResponse;
import com.company.scopery.modules.traceability.aimapping.application.response.MappingRunResponse;
import com.company.scopery.modules.traceability.aimapping.application.response.MappingSuggestionResponse;
import com.company.scopery.modules.traceability.aimapping.application.service.MappingSuggestionQueryService;
import com.company.scopery.modules.traceability.aimapping.http.request.GenerateMappingSuggestionsRequest;
import com.company.scopery.modules.traceability.aimapping.http.request.ReviewMappingSuggestionsRequest;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingScope;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(AiMappingApiPaths.SUGGESTIONS_BASE)
@Tag(name = "Traceability - AI Mapping Suggestions")
public class MappingSuggestionController {

    private final GenerateMappingSuggestionsAction generateAction;
    private final ReviewMappingSuggestionsAction reviewAction;
    private final ApplyMappingDraftAction applyAction;
    private final MappingSuggestionQueryService queryService;

    public MappingSuggestionController(GenerateMappingSuggestionsAction generateAction,
                                       ReviewMappingSuggestionsAction reviewAction,
                                       ApplyMappingDraftAction applyAction,
                                       MappingSuggestionQueryService queryService) {
        this.generateAction = generateAction;
        this.reviewAction = reviewAction;
        this.applyAction = applyAction;
        this.queryService = queryService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Trigger AI mapping suggestion generation for a project")
    public ApiResponse<MappingRunResponse> generate(@PathVariable UUID projectId,
                                                    @Valid @RequestBody GenerateMappingSuggestionsRequest request) {
        MappingRelationType relationType = MappingRelationType.valueOf(request.relationType());
        MappingScope scope = request.scope() != null ? MappingScope.valueOf(request.scope()) : MappingScope.UNMAPPED;
        return ApiResponse.success(generateAction.execute(new GenerateMappingCommand(
                projectId, relationType, scope, request.modelDeploymentId(), null)));
    }

    @GetMapping
    @Operation(summary = "List mapping suggestions with filters")
    public ApiResponse<PageResponse<MappingSuggestionResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam UUID runId,
            @RequestParam(required = false) String relationType,
            @RequestParam(required = false) String reviewStatus,
            @RequestParam(required = false) String confidenceBand,
            @RequestParam(required = false) String decision,
            @RequestParam(required = false) Boolean hasWarning,
            @RequestParam(required = false) UUID sourceId,
            @RequestParam(required = false) UUID targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(queryService.listSuggestions(new ListMappingSuggestionsQuery(
                projectId, runId, relationType, reviewStatus, confidenceBand, decision,
                hasWarning, sourceId, targetId, pageable)));
    }

    @PostMapping("/review")
    @Operation(summary = "Bulk accept or reject mapping suggestions")
    public ApiResponse<Void> review(@PathVariable UUID projectId,
                                    @Valid @RequestBody ReviewMappingSuggestionsRequest request) {
        var decisions = request.decisions().stream()
                .map(d -> new ReviewSuggestionsCommand.SuggestionDecision(d.suggestionId(), d.decision()))
                .toList();
        reviewAction.execute(new ReviewSuggestionsCommand(projectId, decisions, null));
        return ApiResponse.success(null);
    }

    @PostMapping("/{runId}/apply")
    @Operation(summary = "Apply accepted mapping suggestions from a run")
    public ApiResponse<ApplyMappingDraftResponse> apply(@PathVariable UUID projectId,
                                                         @PathVariable UUID runId) {
        return ApiResponse.success(applyAction.execute(new ApplyMappingDraftCommand(projectId, runId, null)));
    }

    @GetMapping("/runs/{runId}")
    @Operation(summary = "Get mapping run details")
    public ApiResponse<MappingRunResponse> getRun(@PathVariable UUID projectId,
                                                   @PathVariable UUID runId) {
        return ApiResponse.success(queryService.getRun(runId));
    }
}
