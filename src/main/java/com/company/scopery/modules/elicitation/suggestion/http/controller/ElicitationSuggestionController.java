package com.company.scopery.modules.elicitation.suggestion.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationApiPaths;
import com.company.scopery.modules.elicitation.suggestion.application.action.ApproveSuggestionItemAction;
import com.company.scopery.modules.elicitation.suggestion.application.action.GenerateSuggestionsAction;
import com.company.scopery.modules.elicitation.suggestion.application.action.RejectSuggestionItemAction;
import com.company.scopery.modules.elicitation.suggestion.application.action.UpdateSuggestionItemAction;
import com.company.scopery.modules.elicitation.suggestion.application.command.ApproveSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.command.GenerateSuggestionsCommand;
import com.company.scopery.modules.elicitation.suggestion.application.command.RejectSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.command.UpdateSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionItemResponse;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionResponse;
import com.company.scopery.modules.elicitation.suggestion.application.service.ElicitationSuggestionQueryService;
import com.company.scopery.modules.elicitation.suggestion.http.request.UpdateSuggestionItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Elicitation - Suggestions")
public class ElicitationSuggestionController {

    private final GenerateSuggestionsAction generateAction;
    private final ApproveSuggestionItemAction approveAction;
    private final RejectSuggestionItemAction rejectAction;
    private final UpdateSuggestionItemAction updateAction;
    private final ElicitationSuggestionQueryService queryService;

    public ElicitationSuggestionController(GenerateSuggestionsAction generateAction,
                                            ApproveSuggestionItemAction approveAction,
                                            RejectSuggestionItemAction rejectAction,
                                            UpdateSuggestionItemAction updateAction,
                                            ElicitationSuggestionQueryService queryService) {
        this.generateAction = generateAction;
        this.approveAction = approveAction;
        this.rejectAction = rejectAction;
        this.updateAction = updateAction;
        this.queryService = queryService;
    }

    @PostMapping(ElicitationApiPaths.ROUND_SUGGESTIONS)
    @Operation(summary = "AI-generate scope change suggestions for a round")
    public ApiResponse<ElicitationSuggestionResponse> generate(@PathVariable UUID roundId) {
        return ApiResponse.success(generateAction.execute(new GenerateSuggestionsCommand(roundId)));
    }

    @GetMapping(ElicitationApiPaths.ROUND_SUGGESTIONS)
    @Operation(summary = "Get suggestions for a round")
    public ApiResponse<ElicitationSuggestionResponse> getByRound(@PathVariable UUID roundId) {
        return ApiResponse.success(queryService.getByRoundId(roundId));
    }

    @PostMapping(ElicitationApiPaths.SUGGESTION_ITEM_APPROVE)
    @Operation(summary = "Approve and execute a suggestion item")
    public ApiResponse<ElicitationSuggestionItemResponse> approve(@PathVariable UUID itemId) {
        return ApiResponse.success(approveAction.execute(new ApproveSuggestionItemCommand(itemId)));
    }

    @PostMapping(ElicitationApiPaths.SUGGESTION_ITEM_REJECT)
    @Operation(summary = "Reject a suggestion item")
    public ApiResponse<ElicitationSuggestionItemResponse> reject(@PathVariable UUID itemId) {
        return ApiResponse.success(rejectAction.execute(new RejectSuggestionItemCommand(itemId)));
    }

    @PatchMapping(ElicitationApiPaths.SUGGESTION_ITEM_CHANGES)
    @Operation(summary = "Edit changesJson of a PENDING or FAILED suggestion item")
    public ApiResponse<ElicitationSuggestionItemResponse> updateChanges(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateSuggestionItemRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateSuggestionItemCommand(itemId, request.changesJson())));
    }
}
