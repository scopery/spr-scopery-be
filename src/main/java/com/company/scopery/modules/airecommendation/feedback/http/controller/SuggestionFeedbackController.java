package com.company.scopery.modules.airecommendation.feedback.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.airecommendation.application.action.SubmitSuggestionFeedbackAction;
import com.company.scopery.modules.airecommendation.application.command.SubmitFeedbackCommand;
import com.company.scopery.modules.airecommendation.application.response.FeedbackResponse;
import com.company.scopery.modules.airecommendation.feedback.http.request.FeedbackRequest;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "AI Recommendations - Feedback")
public class SuggestionFeedbackController {

    private final SubmitSuggestionFeedbackAction feedbackAction;

    public SuggestionFeedbackController(SubmitSuggestionFeedbackAction feedbackAction) {
        this.feedbackAction = feedbackAction;
    }

    @PostMapping(AiRecommendationApiPaths.SUGGESTION_FEEDBACK)
    @Operation(summary = "Submit feedback for a suggestion")
    public ApiResponse<FeedbackResponse> submitFeedback(
            @PathVariable String suggestionRef,
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID actorId,
            @RequestBody FeedbackRequest request) {

        SubmitFeedbackCommand cmd = new SubmitFeedbackCommand(
                workspaceId, actorId, suggestionRef,
                request.helpful(), request.reasonCode(), request.comment(), request.observedOutcome());
        return ApiResponse.success(feedbackAction.execute(cmd));
    }
}
