package com.company.scopery.modules.aiassistant.guide.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiassistant.guide.application.action.ExplainDisabledActionAction;
import com.company.scopery.modules.aiassistant.guide.application.action.ExplainFieldAction;
import com.company.scopery.modules.aiassistant.guide.application.action.ExplainPageAction;
import com.company.scopery.modules.aiassistant.guide.application.command.ExplainDisabledActionCommand;
import com.company.scopery.modules.aiassistant.guide.application.command.ExplainFieldCommand;
import com.company.scopery.modules.aiassistant.guide.application.command.ExplainPageCommand;
import com.company.scopery.modules.aiassistant.guide.application.response.AiSuggestedQuestionResponse;
import com.company.scopery.modules.aiassistant.guide.application.service.AiGuideQueryService;
import com.company.scopery.modules.aiassistant.guide.http.request.ExplainDisabledActionRequest;
import com.company.scopery.modules.aiassistant.guide.http.request.ExplainFieldRequest;
import com.company.scopery.modules.aiassistant.guide.http.request.ExplainPageRequest;
import com.company.scopery.modules.aiassistant.message.application.response.AiSseStartResponse;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "AI Assistant - Guides")
@RestController
@RequestMapping(AiAssistantApiPaths.GUIDES)
public class GuideController {

    private final ExplainPageAction explainPageAction;
    private final ExplainFieldAction explainFieldAction;
    private final ExplainDisabledActionAction explainDisabledActionAction;
    private final AiGuideQueryService guideQueryService;

    public GuideController(ExplainPageAction explainPageAction,
                           ExplainFieldAction explainFieldAction,
                           ExplainDisabledActionAction explainDisabledActionAction,
                           AiGuideQueryService guideQueryService) {
        this.explainPageAction = explainPageAction;
        this.explainFieldAction = explainFieldAction;
        this.explainDisabledActionAction = explainDisabledActionAction;
        this.guideQueryService = guideQueryService;
    }

    @Operation(summary = "Explain a page to the user")
    @PostMapping("/explain-page")
    public ResponseEntity<ApiResponse<AiSseStartResponse>> explainPage(
            @Valid @RequestBody ExplainPageRequest request,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        ExplainPageCommand command = new ExplainPageCommand(
                actorId,
                request.workspaceId(),
                request.projectId(),
                request.pageCode(),
                request.locale()
        );
        AiSseStartResponse response = explainPageAction.execute(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Explain a field to the user")
    @PostMapping("/explain-field")
    public ResponseEntity<ApiResponse<AiSseStartResponse>> explainField(
            @Valid @RequestBody ExplainFieldRequest request,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        ExplainFieldCommand command = new ExplainFieldCommand(
                actorId,
                request.workspaceId(),
                request.projectId(),
                request.pageCode(),
                request.fieldCode(),
                request.locale()
        );
        AiSseStartResponse response = explainFieldAction.execute(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Explain why an action is disabled")
    @PostMapping("/explain-disabled-action")
    public ResponseEntity<ApiResponse<AiSseStartResponse>> explainDisabledAction(
            @Valid @RequestBody ExplainDisabledActionRequest request,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        ExplainDisabledActionCommand command = new ExplainDisabledActionCommand(
                actorId,
                request.workspaceId(),
                request.projectId(),
                request.pageCode(),
                request.actionCode(),
                request.locale()
        );
        AiSseStartResponse response = explainDisabledActionAction.execute(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Get suggested questions for a page")
    @GetMapping("/suggested-questions")
    public ResponseEntity<ApiResponse<List<AiSuggestedQuestionResponse>>> getSuggestedQuestions(
            @RequestParam String pageCode,
            @RequestParam(required = false) String entityType,
            @RequestParam(defaultValue = "en-US") String locale) {

        List<AiSuggestedQuestionResponse> response = guideQueryService.getSuggestedQuestions(
                pageCode, entityType, locale);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
