package com.company.scopery.modules.aiplanning.suggestion.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningApiPaths;
import com.company.scopery.modules.aiplanning.suggestion.application.action.AcceptSuggestionAction;
import com.company.scopery.modules.aiplanning.suggestion.application.action.ApplySuggestionAction;
import com.company.scopery.modules.aiplanning.suggestion.application.action.ArchiveSuggestionAction;
import com.company.scopery.modules.aiplanning.suggestion.application.action.RejectSuggestionAction;
import com.company.scopery.modules.aiplanning.suggestion.application.action.StartSuggestionReviewAction;
import com.company.scopery.modules.aiplanning.suggestion.application.command.AcceptSuggestionCommand;
import com.company.scopery.modules.aiplanning.suggestion.application.command.ApplySuggestionCommand;
import com.company.scopery.modules.aiplanning.suggestion.application.command.ArchiveSuggestionCommand;
import com.company.scopery.modules.aiplanning.suggestion.application.command.RejectSuggestionCommand;
import com.company.scopery.modules.aiplanning.suggestion.application.command.StartSuggestionReviewCommand;
import com.company.scopery.modules.aiplanning.suggestion.application.response.AiPlanningSuggestionResponse;
import com.company.scopery.modules.aiplanning.suggestion.application.response.ApplySuggestionResponse;
import com.company.scopery.modules.aiplanning.suggestion.application.service.AiPlanningSuggestionQueryService;
import com.company.scopery.modules.aiplanning.suggestion.http.request.ApplySuggestionRequest;
import com.company.scopery.modules.aiplanning.suggestion.http.request.RejectSuggestionRequest;
import com.company.scopery.modules.aiplanning.suggestionitem.application.action.AcceptSuggestionItemAction;
import com.company.scopery.modules.aiplanning.suggestionitem.application.action.RejectSuggestionItemAction;
import com.company.scopery.modules.aiplanning.suggestionitem.application.command.AcceptSuggestionItemCommand;
import com.company.scopery.modules.aiplanning.suggestionitem.application.command.RejectSuggestionItemCommand;
import com.company.scopery.modules.aiplanning.suggestionitem.application.response.AiPlanningSuggestionItemResponse;
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
@RequestMapping(AiPlanningApiPaths.SUGGESTIONS)
@Tag(name = "AI Planning - Suggestions")
public class AiPlanningSuggestionController {
    private final AiPlanningSuggestionQueryService query;
    private final StartSuggestionReviewAction startReview;
    private final AcceptSuggestionAction accept;
    private final RejectSuggestionAction reject;
    private final ArchiveSuggestionAction archive;
    private final ApplySuggestionAction apply;
    private final AcceptSuggestionItemAction acceptItem;
    private final RejectSuggestionItemAction rejectItem;

    public AiPlanningSuggestionController(AiPlanningSuggestionQueryService query,
                                          StartSuggestionReviewAction startReview,
                                          AcceptSuggestionAction accept,
                                          RejectSuggestionAction reject,
                                          ArchiveSuggestionAction archive,
                                          ApplySuggestionAction apply,
                                          AcceptSuggestionItemAction acceptItem,
                                          RejectSuggestionItemAction rejectItem) {
        this.query = query; this.startReview = startReview; this.accept = accept; this.reject = reject;
        this.archive = archive; this.apply = apply; this.acceptItem = acceptItem; this.rejectItem = rejectItem;
    }

    @GetMapping
    @Operation(summary = "List AI planning suggestions")
    public ApiResponse<List<AiPlanningSuggestionResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{suggestionId}")
    @Operation(summary = "Get AI planning suggestion")
    public ApiResponse<AiPlanningSuggestionResponse> get(@PathVariable UUID projectId, @PathVariable UUID suggestionId) {
        return ApiResponse.success(query.get(projectId, suggestionId));
    }

    @PostMapping("/{suggestionId}/start-review")
    @Operation(summary = "Start suggestion review")
    public ApiResponse<AiPlanningSuggestionResponse> startReview(@PathVariable UUID projectId, @PathVariable UUID suggestionId) {
        return ApiResponse.success(startReview.execute(new StartSuggestionReviewCommand(projectId, suggestionId)));
    }

    @PostMapping("/{suggestionId}/accept")
    @Operation(summary = "Accept suggestion")
    public ApiResponse<AiPlanningSuggestionResponse> accept(@PathVariable UUID projectId, @PathVariable UUID suggestionId) {
        return ApiResponse.success(accept.execute(new AcceptSuggestionCommand(projectId, suggestionId)));
    }

    @PostMapping("/{suggestionId}/reject")
    @Operation(summary = "Reject suggestion")
    public ApiResponse<AiPlanningSuggestionResponse> reject(@PathVariable UUID projectId, @PathVariable UUID suggestionId,
                                                            @Valid @RequestBody RejectSuggestionRequest request) {
        return ApiResponse.success(reject.execute(new RejectSuggestionCommand(projectId, suggestionId, request.reason())));
    }

    @PostMapping("/{suggestionId}/archive")
    @Operation(summary = "Archive suggestion")
    public ApiResponse<AiPlanningSuggestionResponse> archive(@PathVariable UUID projectId, @PathVariable UUID suggestionId) {
        return ApiResponse.success(archive.execute(new ArchiveSuggestionCommand(projectId, suggestionId)));
    }

    @PostMapping("/{suggestionId}/apply")
    @Operation(summary = "Apply accepted suggestion items safely")
    public ApiResponse<ApplySuggestionResponse> apply(@PathVariable UUID projectId, @PathVariable UUID suggestionId,
                                                      @RequestBody(required = false) ApplySuggestionRequest request) {
        ApplySuggestionRequest req = request == null ? new ApplySuggestionRequest(null, true, null) : request;
        return ApiResponse.success(apply.execute(new ApplySuggestionCommand(
                projectId, suggestionId, req.applyMode(), req.requireChangeRequestIfBaselined())));
    }

    @GetMapping("/{suggestionId}/items")
    @Operation(summary = "List suggestion items")
    public ApiResponse<List<AiPlanningSuggestionItemResponse>> listItems(@PathVariable UUID projectId,
                                                                         @PathVariable UUID suggestionId) {
        return ApiResponse.success(query.listItems(projectId, suggestionId));
    }

    @GetMapping("/{suggestionId}/items/{itemId}")
    @Operation(summary = "Get suggestion item")
    public ApiResponse<AiPlanningSuggestionItemResponse> getItem(@PathVariable UUID projectId,
                                                                 @PathVariable UUID suggestionId,
                                                                 @PathVariable UUID itemId) {
        return ApiResponse.success(query.getItem(projectId, suggestionId, itemId));
    }

    @PostMapping("/{suggestionId}/items/{itemId}/accept")
    @Operation(summary = "Accept suggestion item")
    public ApiResponse<AiPlanningSuggestionItemResponse> acceptItem(@PathVariable UUID projectId,
                                                                    @PathVariable UUID suggestionId,
                                                                    @PathVariable UUID itemId) {
        return ApiResponse.success(acceptItem.execute(new AcceptSuggestionItemCommand(projectId, suggestionId, itemId)));
    }

    @PostMapping("/{suggestionId}/items/{itemId}/reject")
    @Operation(summary = "Reject suggestion item")
    public ApiResponse<AiPlanningSuggestionItemResponse> rejectItem(@PathVariable UUID projectId,
                                                                    @PathVariable UUID suggestionId,
                                                                    @PathVariable UUID itemId) {
        return ApiResponse.success(rejectItem.execute(new RejectSuggestionItemCommand(projectId, suggestionId, itemId)));
    }
}
