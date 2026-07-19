package com.company.scopery.modules.aiassistant.feedback.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiassistant.feedback.application.action.CreateFeedbackAction;
import com.company.scopery.modules.aiassistant.feedback.application.command.CreateFeedbackCommand;
import com.company.scopery.modules.aiassistant.feedback.application.response.AiAnswerFeedbackResponse;
import com.company.scopery.modules.aiassistant.feedback.http.request.CreateFeedbackRequest;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Assistant - Feedback")
@RestController
@RequestMapping(AiAssistantApiPaths.FEEDBACKS)
public class FeedbackController {

    private final CreateFeedbackAction createFeedbackAction;

    public FeedbackController(CreateFeedbackAction createFeedbackAction) {
        this.createFeedbackAction = createFeedbackAction;
    }

    @Operation(summary = "Submit feedback for an AI assistant answer")
    @PostMapping
    public ResponseEntity<ApiResponse<AiAnswerFeedbackResponse>> createFeedback(
            @Valid @RequestBody CreateFeedbackRequest request,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        CreateFeedbackCommand command = new CreateFeedbackCommand(
                request.conversationId(),
                request.messageId(),
                actorId,
                request.rating(),
                request.reasonCode(),
                request.comment()
        );
        AiAnswerFeedbackResponse response = createFeedbackAction.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
