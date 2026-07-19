package com.company.scopery.modules.airecommendation.suggestion.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.airecommendation.application.action.AcceptSuggestionAction;
import com.company.scopery.modules.airecommendation.application.action.EditSuggestionAction;
import com.company.scopery.modules.airecommendation.application.action.PrepareApplySuggestionAction;
import com.company.scopery.modules.airecommendation.application.action.RejectSuggestionAction;
import com.company.scopery.modules.airecommendation.application.action.SuppressSuggestionAction;
import com.company.scopery.modules.airecommendation.application.action.ViewSuggestionAction;
import com.company.scopery.modules.airecommendation.application.command.AcceptSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.command.EditSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.command.PrepareApplyCommand;
import com.company.scopery.modules.airecommendation.application.command.RejectSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.command.SuppressSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.command.ViewSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.query.ListEntitySuggestionsQuery;
import com.company.scopery.modules.airecommendation.application.query.ListProjectSuggestionsQuery;
import com.company.scopery.modules.airecommendation.application.response.AcceptSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.response.EditSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.response.PrepareApplyResponse;
import com.company.scopery.modules.airecommendation.application.response.RejectSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.response.SuggestionDetailResponse;
import com.company.scopery.modules.airecommendation.application.response.SuggestionSummaryResponse;
import com.company.scopery.modules.airecommendation.application.response.SuppressSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.response.ViewSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.service.SuggestionQueryService;
import com.company.scopery.modules.airecommendation.domain.enums.SuppressionScopeType;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationApiPaths;
import com.company.scopery.modules.airecommendation.suggestion.http.request.AcceptSuggestionRequest;
import com.company.scopery.modules.airecommendation.suggestion.http.request.EditSuggestionRequest;
import com.company.scopery.modules.airecommendation.suggestion.http.request.PrepareApplySuggestionRequest;
import com.company.scopery.modules.airecommendation.suggestion.http.request.RejectSuggestionRequest;
import com.company.scopery.modules.airecommendation.suggestion.http.request.SuppressSuggestionRequest;
import com.company.scopery.modules.airecommendation.suggestion.http.request.ViewSuggestionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Tag(name = "AI Recommendations - Suggestions")
public class SuggestionController {

    private final SuggestionQueryService queryService;
    private final ViewSuggestionAction viewAction;
    private final EditSuggestionAction editAction;
    private final AcceptSuggestionAction acceptAction;
    private final RejectSuggestionAction rejectAction;
    private final SuppressSuggestionAction suppressAction;
    private final PrepareApplySuggestionAction prepareApplyAction;

    public SuggestionController(SuggestionQueryService queryService,
                                 ViewSuggestionAction viewAction,
                                 EditSuggestionAction editAction,
                                 AcceptSuggestionAction acceptAction,
                                 RejectSuggestionAction rejectAction,
                                 SuppressSuggestionAction suppressAction,
                                 PrepareApplySuggestionAction prepareApplyAction) {
        this.queryService = queryService;
        this.viewAction = viewAction;
        this.editAction = editAction;
        this.acceptAction = acceptAction;
        this.rejectAction = rejectAction;
        this.suppressAction = suppressAction;
        this.prepareApplyAction = prepareApplyAction;
    }

    @GetMapping(AiRecommendationApiPaths.PROJECT_SUGGESTIONS)
    @Operation(summary = "List suggestions for a project")
    public ApiResponse<PageResponse<SuggestionSummaryResponse>> listForProject(
            @PathVariable UUID projectId,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> severity,
            @RequestParam(required = false) String packCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String targetEntityType,
            @RequestParam(defaultValue = "false") boolean includeLegacyPhase21,
            @RequestParam(defaultValue = "false") boolean includeExpired,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        ListProjectSuggestionsQuery query = new ListProjectSuggestionsQuery(
                workspaceId, projectId, null, status, severity, packCode, type,
                targetEntityType, includeLegacyPhase21, includeExpired, pageable);
        return ApiResponse.success(queryService.listForProject(query));
    }

    @GetMapping(AiRecommendationApiPaths.ENTITY_SUGGESTIONS)
    @Operation(summary = "List suggestions for a specific entity")
    public ApiResponse<PageResponse<SuggestionSummaryResponse>> listForEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ListEntitySuggestionsQuery query = new ListEntitySuggestionsQuery(
                workspaceId, null, entityType, entityId, projectId, false, pageable);
        return ApiResponse.success(queryService.listForEntity(query));
    }

    @GetMapping(AiRecommendationApiPaths.SUGGESTION_BY_REF)
    @Operation(summary = "Get suggestion detail by ref")
    public ApiResponse<SuggestionDetailResponse> getDetail(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId) {
        return ApiResponse.success(queryService.getDetail(suggestionRef, workspaceId, actorId));
    }

    @PostMapping(AiRecommendationApiPaths.SUGGESTION_VIEW)
    @Operation(summary = "Mark suggestion as viewed")
    public ApiResponse<ViewSuggestionResponse> view(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @RequestBody(required = false) ViewSuggestionRequest request) {

        long expectedVersion = request != null && request.expectedVersion() != null ? request.expectedVersion() : 0;
        String idempotencyKey = request != null ? request.idempotencyKey() : null;
        ViewSuggestionCommand cmd = new ViewSuggestionCommand(
                workspaceId, actorId, suggestionRef, expectedVersion, idempotencyKey, MDC.get("traceId"));
        return ApiResponse.success(viewAction.execute(cmd));
    }

    @PatchMapping(AiRecommendationApiPaths.SUGGESTION_EDIT)
    @Operation(summary = "Edit suggestion items")
    public ApiResponse<EditSuggestionResponse> edit(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @Valid @RequestBody EditSuggestionRequest request) {

        List<EditSuggestionCommand.EditItemCommand> items = request.items() == null ? List.of() :
                request.items().stream()
                        .map(i -> new EditSuggestionCommand.EditItemCommand(i.itemId(), i.proposedPayload()))
                        .collect(Collectors.toList());
        EditSuggestionCommand cmd = new EditSuggestionCommand(
                workspaceId, actorId, suggestionRef, request.expectedVersion(),
                request.idempotencyKey(), items, request.comment(), MDC.get("traceId"));
        return ApiResponse.success(editAction.execute(cmd));
    }

    @PostMapping(AiRecommendationApiPaths.SUGGESTION_ACCEPT)
    @Operation(summary = "Accept a suggestion")
    public ApiResponse<AcceptSuggestionResponse> accept(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @RequestBody(required = false) AcceptSuggestionRequest request) {

        long expectedVersion = request != null && request.expectedVersion() != null ? request.expectedVersion() : 0;
        AcceptSuggestionCommand cmd = new AcceptSuggestionCommand(
                workspaceId, actorId, suggestionRef, expectedVersion,
                request != null ? request.idempotencyKey() : null,
                request != null ? request.comment() : null,
                MDC.get("traceId"));
        return ApiResponse.success(acceptAction.execute(cmd));
    }

    @PostMapping(AiRecommendationApiPaths.SUGGESTION_REJECT)
    @Operation(summary = "Reject a suggestion")
    public ApiResponse<RejectSuggestionResponse> reject(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @RequestBody(required = false) RejectSuggestionRequest request) {

        long expectedVersion = request != null && request.expectedVersion() != null ? request.expectedVersion() : 0;
        RejectSuggestionCommand cmd = new RejectSuggestionCommand(
                workspaceId, actorId, suggestionRef, expectedVersion,
                request != null ? request.idempotencyKey() : null,
                request != null ? request.reasonCode() : null,
                request != null ? request.comment() : null,
                MDC.get("traceId"));
        return ApiResponse.success(rejectAction.execute(cmd));
    }

    @PostMapping(AiRecommendationApiPaths.SUGGESTION_SUPPRESS)
    @Operation(summary = "Suppress a suggestion")
    public ApiResponse<SuppressSuggestionResponse> suppress(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @Valid @RequestBody SuppressSuggestionRequest request) {

        SuppressionScopeType scopeType;
        try {
            scopeType = SuppressionScopeType.valueOf(request.scopeType().toUpperCase());
        } catch (IllegalArgumentException e) {
            scopeType = SuppressionScopeType.TARGET;
        }
        SuppressSuggestionCommand cmd = new SuppressSuggestionCommand(
                workspaceId, actorId, suggestionRef, request.expectedVersion(),
                request.idempotencyKey(), scopeType, request.durationDays(),
                request.reasonCode(), request.comment(), MDC.get("traceId"));
        return ApiResponse.success(suppressAction.execute(cmd));
    }

    @PostMapping(AiRecommendationApiPaths.SUGGESTION_PREPARE_APPLY)
    @Operation(summary = "Prepare apply for an accepted suggestion")
    public ApiResponse<PrepareApplyResponse> prepareApply(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @RequestBody(required = false) PrepareApplySuggestionRequest request) {

        long expectedVersion = request != null && request.expectedVersion() != null ? request.expectedVersion() : 0;
        PrepareApplyCommand cmd = new PrepareApplyCommand(
                workspaceId, actorId, suggestionRef, expectedVersion,
                request != null && request.selectedItemIds() != null ? request.selectedItemIds() : List.of(),
                request != null ? request.idempotencyKey() : null,
                null, MDC.get("traceId"));
        return ApiResponse.success(prepareApplyAction.execute(cmd));
    }
}
