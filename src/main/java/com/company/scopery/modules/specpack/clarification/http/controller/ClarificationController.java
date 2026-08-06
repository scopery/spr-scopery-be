package com.company.scopery.modules.specpack.clarification.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.specpack.clarification.application.action.AnswerClarificationAction;
import com.company.scopery.modules.specpack.clarification.application.action.CreateClarificationAction;
import com.company.scopery.modules.specpack.clarification.application.action.DeferClarificationAction;
import com.company.scopery.modules.specpack.clarification.application.action.ImportClarificationsAction;
import com.company.scopery.modules.specpack.clarification.application.command.AnswerClarificationCommand;
import com.company.scopery.modules.specpack.clarification.application.command.CreateClarificationCommand;
import com.company.scopery.modules.specpack.clarification.application.command.DeferClarificationCommand;
import com.company.scopery.modules.specpack.clarification.application.response.ClarificationResponse;
import com.company.scopery.modules.specpack.clarification.application.service.ClarificationQueryService;
import com.company.scopery.modules.specpack.clarification.http.request.AnswerClarificationRequest;
import com.company.scopery.modules.specpack.clarification.http.request.CreateClarificationRequest;
import com.company.scopery.modules.specpack.shared.constant.SpecPackApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Spec Pack - Clarifications")
@RestController
@RequestMapping(SpecPackApiPaths.CLARIFICATIONS)
public class ClarificationController {

    private final CreateClarificationAction createAction;
    private final AnswerClarificationAction answerAction;
    private final DeferClarificationAction deferAction;
    private final ImportClarificationsAction importAction;
    private final ClarificationQueryService queryService;

    public ClarificationController(CreateClarificationAction createAction,
                                    AnswerClarificationAction answerAction,
                                    DeferClarificationAction deferAction,
                                    ImportClarificationsAction importAction,
                                    ClarificationQueryService queryService) {
        this.createAction = createAction;
        this.answerAction = answerAction;
        this.deferAction = deferAction;
        this.importAction = importAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a clarification in an agent session")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ClarificationResponse> create(@PathVariable UUID projectId,
                                                      @PathVariable UUID sessionId,
                                                      @Valid @RequestBody CreateClarificationRequest request) {
        return ApiResponse.success(createAction.execute(new CreateClarificationCommand(
                projectId, sessionId, request.code(), request.question(), request.priority(), request.source()
        )));
    }

    @Operation(summary = "List clarifications for an agent session")
    @GetMapping
    public ApiResponse<List<ClarificationResponse>> list(@PathVariable UUID projectId,
                                                          @PathVariable UUID sessionId,
                                                          @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return ApiResponse.success(queryService.listBySessionAndStatus(sessionId, status));
        }
        return ApiResponse.success(queryService.listBySession(sessionId));
    }

    @Operation(summary = "Get a clarification by ID")
    @GetMapping("/{clarificationId}")
    public ApiResponse<ClarificationResponse> getById(@PathVariable UUID projectId,
                                                       @PathVariable UUID sessionId,
                                                       @PathVariable UUID clarificationId) {
        return ApiResponse.success(queryService.getById(clarificationId));
    }

    @Operation(summary = "Answer a clarification")
    @PutMapping("/{clarificationId}/answer")
    public ApiResponse<ClarificationResponse> answer(@PathVariable UUID projectId,
                                                      @PathVariable UUID sessionId,
                                                      @PathVariable UUID clarificationId,
                                                      @Valid @RequestBody AnswerClarificationRequest request) {
        return ApiResponse.success(answerAction.execute(new AnswerClarificationCommand(
                projectId, sessionId, clarificationId, request.answer()
        )));
    }

    @Operation(summary = "Defer a clarification")
    @PutMapping("/{clarificationId}/defer")
    public ApiResponse<ClarificationResponse> defer(@PathVariable UUID projectId,
                                                     @PathVariable UUID sessionId,
                                                     @PathVariable UUID clarificationId) {
        return ApiResponse.success(deferAction.execute(new DeferClarificationCommand(
                projectId, sessionId, clarificationId
        )));
    }

    @Operation(summary = "Bulk import clarifications into an agent session")
    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<List<ClarificationResponse>> importClarifications(
            @PathVariable UUID projectId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody List<@Valid CreateClarificationRequest> requests) {
        List<CreateClarificationCommand> commands = requests.stream()
                .map(r -> new CreateClarificationCommand(projectId, sessionId, r.code(), r.question(), r.priority(), r.source()))
                .collect(Collectors.toList());
        return ApiResponse.success(importAction.execute(commands));
    }
}
