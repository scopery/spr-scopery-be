package com.company.scopery.modules.elicitation.round.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.elicitation.round.application.action.GenerateRoundAction;
import com.company.scopery.modules.elicitation.round.application.action.SubmitRoundAction;
import com.company.scopery.modules.elicitation.round.application.command.GenerateRoundCommand;
import com.company.scopery.modules.elicitation.round.application.command.SubmitRoundCommand;
import com.company.scopery.modules.elicitation.round.application.response.ElicitationRoundResponse;
import com.company.scopery.modules.elicitation.round.application.response.SubmitRoundResponse;
import com.company.scopery.modules.elicitation.round.application.service.ElicitationRoundQueryService;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@Tag(name = "Elicitation - Rounds")
public class ElicitationRoundController {

    private final GenerateRoundAction generateAction;
    private final SubmitRoundAction submitAction;
    private final ElicitationRoundQueryService queryService;

    public ElicitationRoundController(GenerateRoundAction generateAction,
                                       SubmitRoundAction submitAction,
                                       ElicitationRoundQueryService queryService) {
        this.generateAction = generateAction;
        this.submitAction = submitAction;
        this.queryService = queryService;
    }

    @PostMapping(value = ElicitationApiPaths.SESSION_ROUNDS_GENERATE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Generate next round of questions via SSE stream")
    public SseEmitter generate(@PathVariable UUID projectId, @PathVariable UUID sessionId) {
        SseEmitter emitter = new SseEmitter(300_000L);
        CompletableFuture.runAsync(() -> {
            try {
                generateAction.executeStreaming(
                        new GenerateRoundCommand(projectId, sessionId),
                        question -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("question")
                                        .data(question, MediaType.APPLICATION_JSON));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        round -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("round")
                                        .data(round, MediaType.APPLICATION_JSON));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        }
                );
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error")
                            .data(e.getMessage() != null ? e.getMessage() : "Generation failed"));
                } catch (IOException ignored) {}
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @PostMapping(ElicitationApiPaths.ROUND_SUBMIT)
    @Operation(summary = "Submit round for AI evaluation (lock + evaluate in one step)")
    public ApiResponse<SubmitRoundResponse> submit(@PathVariable UUID projectId,
                                                    @PathVariable UUID sessionId,
                                                    @PathVariable UUID roundId) {
        return ApiResponse.success(submitAction.execute(new SubmitRoundCommand(projectId, sessionId, roundId)));
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
}
