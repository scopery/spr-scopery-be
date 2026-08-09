package com.company.scopery.modules.elicitation.question.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.elicitation.question.application.action.AnswerQuestionAction;
import com.company.scopery.modules.elicitation.question.application.action.EvaluateAnswersAction;
import com.company.scopery.modules.elicitation.question.application.action.GenerateQuestionsAction;
import com.company.scopery.modules.elicitation.question.application.action.SkipQuestionAction;
import com.company.scopery.modules.elicitation.question.application.command.AnswerQuestionCommand;
import com.company.scopery.modules.elicitation.question.application.command.EvaluateAnswersCommand;
import com.company.scopery.modules.elicitation.question.application.command.GenerateQuestionsCommand;
import com.company.scopery.modules.elicitation.question.application.command.SkipQuestionCommand;
import com.company.scopery.modules.elicitation.question.application.response.ElicitationQuestionResponse;
import com.company.scopery.modules.elicitation.question.application.service.ElicitationQuestionQueryService;
import com.company.scopery.modules.elicitation.question.http.request.AnswerQuestionRequest;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Elicitation - Questions")
public class ElicitationQuestionController {

    private final GenerateQuestionsAction generateAction;
    private final AnswerQuestionAction answerAction;
    private final SkipQuestionAction skipAction;
    private final EvaluateAnswersAction evaluateAction;
    private final ElicitationQuestionQueryService queryService;

    public ElicitationQuestionController(GenerateQuestionsAction generateAction,
                                          AnswerQuestionAction answerAction,
                                          SkipQuestionAction skipAction,
                                          EvaluateAnswersAction evaluateAction,
                                          ElicitationQuestionQueryService queryService) {
        this.generateAction = generateAction;
        this.answerAction = answerAction;
        this.skipAction = skipAction;
        this.evaluateAction = evaluateAction;
        this.queryService = queryService;
    }

    @GetMapping(ElicitationApiPaths.SESSION_QUESTIONS)
    @Operation(summary = "List all questions for an elicitation session")
    public ApiResponse<List<ElicitationQuestionResponse>> list(@PathVariable UUID projectId,
                                                                @PathVariable UUID sessionId) {
        return ApiResponse.success(queryService.listBySession(sessionId));
    }

    @PostMapping(ElicitationApiPaths.GENERATE_QUESTIONS)
    @Operation(summary = "AI-generate clarification questions for a session")
    public ApiResponse<List<ElicitationQuestionResponse>> generate(@PathVariable UUID projectId,
                                                                    @PathVariable UUID sessionId) {
        return ApiResponse.success(generateAction.execute(new GenerateQuestionsCommand(projectId, sessionId)));
    }

    @PostMapping(ElicitationApiPaths.EVALUATE_ANSWERS)
    @Operation(summary = "AI-evaluate all answered questions in a session")
    public ApiResponse<List<ElicitationQuestionResponse>> evaluate(@PathVariable UUID projectId,
                                                                    @PathVariable UUID sessionId) {
        return ApiResponse.success(evaluateAction.execute(new EvaluateAnswersCommand(projectId, sessionId)));
    }

    @PatchMapping(ElicitationApiPaths.QUESTION_ANSWER)
    @Operation(summary = "Submit an answer for a question")
    public ApiResponse<ElicitationQuestionResponse> answer(@PathVariable UUID projectId,
                                                            @PathVariable UUID sessionId,
                                                            @PathVariable UUID questionId,
                                                            @Valid @RequestBody AnswerQuestionRequest request) {
        return ApiResponse.success(answerAction.execute(
                new AnswerQuestionCommand(projectId, sessionId, questionId, request.answerText())));
    }

    @PatchMapping(ElicitationApiPaths.QUESTION_SKIP)
    @Operation(summary = "Skip a question")
    public ApiResponse<ElicitationQuestionResponse> skip(@PathVariable UUID projectId,
                                                          @PathVariable UUID sessionId,
                                                          @PathVariable UUID questionId) {
        return ApiResponse.success(skipAction.execute(new SkipQuestionCommand(projectId, sessionId, questionId)));
    }
}
