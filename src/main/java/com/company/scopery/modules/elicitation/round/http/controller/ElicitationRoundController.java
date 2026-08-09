package com.company.scopery.modules.elicitation.round.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.elicitation.round.application.action.SubmitRoundAction;
import com.company.scopery.modules.elicitation.round.application.command.SubmitRoundCommand;
import com.company.scopery.modules.elicitation.round.application.response.ElicitationRoundResponse;
import com.company.scopery.modules.elicitation.round.application.service.ElicitationRoundQueryService;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Elicitation - Rounds")
public class ElicitationRoundController {

    private final SubmitRoundAction submitAction;
    private final ElicitationRoundQueryService queryService;

    public ElicitationRoundController(SubmitRoundAction submitAction,
                                       ElicitationRoundQueryService queryService) {
        this.submitAction = submitAction;
        this.queryService = queryService;
    }

    @GetMapping(ElicitationApiPaths.SESSION_ROUNDS)
    @Operation(summary = "List all rounds for an elicitation session")
    public ApiResponse<List<ElicitationRoundResponse>> listBySession(@PathVariable UUID projectId,
                                                                      @PathVariable UUID sessionId) {
        return ApiResponse.success(queryService.listBySession(sessionId));
    }

    @GetMapping(ElicitationApiPaths.ROUND_BY_ID)
    @Operation(summary = "Get elicitation round by ID")
    public ApiResponse<ElicitationRoundResponse> getById(@PathVariable UUID roundId) {
        return ApiResponse.success(queryService.getById(roundId));
    }

    @PostMapping(ElicitationApiPaths.ROUND_SUBMIT)
    @Operation(summary = "Submit elicitation round for AI suggestion generation")
    public ApiResponse<ElicitationRoundResponse> submit(@PathVariable UUID roundId) {
        return ApiResponse.success(submitAction.execute(new SubmitRoundCommand(roundId)));
    }
}
