package com.company.scopery.modules.aiassistant.message.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiassistant.infrastructure.sse.AiAssistantSseStreamService;
import com.company.scopery.modules.aiassistant.message.application.action.CancelMessageAction;
import com.company.scopery.modules.aiassistant.message.application.action.SendMessageAction;
import com.company.scopery.modules.aiassistant.message.application.command.CancelMessageCommand;
import com.company.scopery.modules.aiassistant.message.application.command.SendMessageCommand;
import com.company.scopery.modules.aiassistant.message.application.response.AiMessageResponse;
import com.company.scopery.modules.aiassistant.message.application.response.AiSseStartResponse;
import com.company.scopery.modules.aiassistant.message.application.service.AiMessageQueryService;
import com.company.scopery.modules.aiassistant.message.http.request.SendMessageRequest;
import com.company.scopery.modules.aiassistant.shared.config.AiAssistantProperties;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Tag(name = "AI Assistant - Messages")
@RestController
public class MessageController {

    private final AiMessageQueryService queryService;
    private final SendMessageAction sendMessageAction;
    private final CancelMessageAction cancelMessageAction;
    private final AiAssistantSseStreamService sseStreamService;
    private final AiAssistantProperties properties;

    public MessageController(AiMessageQueryService queryService,
                             SendMessageAction sendMessageAction,
                             CancelMessageAction cancelMessageAction,
                             AiAssistantSseStreamService sseStreamService,
                             AiAssistantProperties properties) {
        this.queryService = queryService;
        this.sendMessageAction = sendMessageAction;
        this.cancelMessageAction = cancelMessageAction;
        this.sseStreamService = sseStreamService;
        this.properties = properties;
    }

    @Operation(summary = "Send a user message and start AI turn")
    @PostMapping(AiAssistantApiPaths.MESSAGES_BY_CONV)
    public ResponseEntity<ApiResponse<AiSseStartResponse>> sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId,
            @RequestHeader(value = "X-Workspace-Id", required = false) UUID workspaceId) {

        SendMessageCommand command = new SendMessageCommand(
                conversationId,
                actorId,
                workspaceId,
                request.sourceProjectId(),
                request.content(),
                request.idempotencyKey(),
                request.modelProvider(),
                request.modelName(),
                request.pageCode(),
                request.entityType(),
                request.entityId()
        );
        AiSseStartResponse response = sendMessageAction.execute(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(response));
    }

    @Operation(summary = "List messages in a conversation")
    @GetMapping(AiAssistantApiPaths.MESSAGES_BY_CONV)
    public ResponseEntity<ApiResponse<PageResponse<AiMessageResponse>>> listMessages(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AiMessageResponse> result = queryService.listByConversation(conversationId, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Get a message by ID")
    @GetMapping(AiAssistantApiPaths.MESSAGE_BY_ID)
    public ResponseEntity<ApiResponse<AiMessageResponse>> getMessage(
            @PathVariable UUID messageId,
            @RequestParam(required = false) UUID conversationId) {

        AiMessageResponse response = conversationId != null
                ? queryService.getById(messageId, conversationId)
                : queryService.getById(messageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Stream SSE events for a message")
    @GetMapping(value = AiAssistantApiPaths.MESSAGE_BY_ID + "/stream",
                produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessage(
            @PathVariable UUID messageId,
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

        long timeoutMs = properties.getEmitterTimeout().toMillis();
        SseEmitter emitter = sseStreamService.create(messageId, timeoutMs);

        if (lastEventId != null && !lastEventId.isBlank()) {
            try {
                long afterSequence = Long.parseLong(lastEventId);
                sseStreamService.replay(emitter, messageId, afterSequence);
            } catch (NumberFormatException ignored) {
                // Invalid Last-Event-ID — start fresh without replay
            }
        }

        return emitter;
    }

    @Operation(summary = "Request cancellation of an in-progress message")
    @PostMapping(AiAssistantApiPaths.MESSAGE_BY_ID + "/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelMessage(
            @PathVariable UUID messageId,
            @RequestParam UUID conversationId,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        CancelMessageCommand command = new CancelMessageCommand(messageId, conversationId, actorId);
        cancelMessageAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
