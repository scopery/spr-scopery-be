package com.company.scopery.modules.elicitation.suggestion.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationApiPaths;
import com.company.scopery.modules.elicitation.suggestion.application.action.ApproveSuggestionItemAction;
import com.company.scopery.modules.elicitation.suggestion.application.action.GenerateSuggestionsAction;
import com.company.scopery.modules.elicitation.suggestion.application.action.RejectSuggestionItemAction;
import com.company.scopery.modules.elicitation.suggestion.application.command.ApproveSuggestionItemCommand;
import org.springframework.beans.factory.annotation.Qualifier;
import com.company.scopery.modules.elicitation.suggestion.application.command.RejectSuggestionItemCommand;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionItemResponse;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionResponse;
import com.company.scopery.modules.elicitation.suggestion.application.service.ElicitationSuggestionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Elicitation - Suggestions")
public class ElicitationSuggestionController {

    private final GenerateSuggestionsAction generateAction;
    private final ApproveSuggestionItemAction approveAction;
    private final RejectSuggestionItemAction rejectAction;
    private final ElicitationSuggestionQueryService queryService;

    public ElicitationSuggestionController(GenerateSuggestionsAction generateAction,
                                            ApproveSuggestionItemAction approveAction,
                                            @Qualifier("elicitationRejectSuggestionItemAction") RejectSuggestionItemAction rejectAction,
                                            ElicitationSuggestionQueryService queryService) {
        this.generateAction = generateAction;
        this.approveAction = approveAction;
        this.rejectAction = rejectAction;
        this.queryService = queryService;
    }

    @PostMapping(ElicitationApiPaths.ROUND_SUGGESTIONS)
    @Operation(summary = "AI-generate scope change suggestions for a round")
    public ApiResponse<ElicitationSuggestionResponse> generate(@PathVariable UUID roundId) {
        return ApiResponse.success(generateAction.execute(roundId));
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
}
