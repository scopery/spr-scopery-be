package com.company.scopery.modules.aiassistant.conversation.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiassistant.conversation.application.action.ArchiveConversationAction;
import com.company.scopery.modules.aiassistant.conversation.application.action.CreateConversationAction;
import com.company.scopery.modules.aiassistant.conversation.application.action.DeleteConversationAction;
import com.company.scopery.modules.aiassistant.conversation.application.action.UpdateConversationTitleAction;
import com.company.scopery.modules.aiassistant.conversation.application.command.ArchiveConversationCommand;
import com.company.scopery.modules.aiassistant.conversation.application.command.CreateConversationCommand;
import com.company.scopery.modules.aiassistant.conversation.application.command.DeleteConversationCommand;
import com.company.scopery.modules.aiassistant.conversation.application.command.UpdateConversationTitleCommand;
import com.company.scopery.modules.aiassistant.conversation.application.response.AiConversationResponse;
import com.company.scopery.modules.aiassistant.conversation.application.service.AiConversationQueryService;
import com.company.scopery.modules.aiassistant.conversation.http.request.CreateConversationRequest;
import com.company.scopery.modules.aiassistant.conversation.http.request.UpdateConversationTitleRequest;
import com.company.scopery.modules.aiassistant.domain.enums.CapabilityLevel;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationType;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import com.company.scopery.modules.aiassistant.shared.util.AiAssistantEnumParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Assistant - Conversations")
@RestController
@RequestMapping(AiAssistantApiPaths.CONVERSATIONS)
public class ConversationController {

    private final AiConversationQueryService queryService;
    private final CreateConversationAction createAction;
    private final UpdateConversationTitleAction updateTitleAction;
    private final ArchiveConversationAction archiveAction;
    private final DeleteConversationAction deleteAction;

    public ConversationController(AiConversationQueryService queryService,
                                   CreateConversationAction createAction,
                                   UpdateConversationTitleAction updateTitleAction,
                                   ArchiveConversationAction archiveAction,
                                   DeleteConversationAction deleteAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateTitleAction = updateTitleAction;
        this.archiveAction = archiveAction;
        this.deleteAction = deleteAction;
    }

    @Operation(summary = "Create a new AI assistant conversation")
    @PostMapping
    public ResponseEntity<ApiResponse<AiConversationResponse>> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId,
            @RequestHeader(value = "X-Workspace-Id", required = false) UUID workspaceId) {

        ConversationType type = AiAssistantEnumParser.parseRequired(
                ConversationType.class, request.conversationType(), "conversationType");
        CapabilityLevel level = AiAssistantEnumParser.parseRequired(
                CapabilityLevel.class, request.capabilityLevel(), "capabilityLevel");

        CreateConversationCommand command = new CreateConversationCommand(
                actorId,
                request.workspaceId() != null ? request.workspaceId() : workspaceId,
                request.projectId(),
                type,
                level,
                request.assistantAgentId(),
                request.title()
        );
        AiConversationResponse response = createAction.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "List active conversations for the current actor")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AiConversationResponse>>> listConversations(
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId,
            @RequestHeader(value = "X-Workspace-Id", required = false) UUID workspaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AiConversationResponse> result = queryService.listByActor(workspaceId, actorId, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Get conversation by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AiConversationResponse>> getConversation(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        AiConversationResponse response = queryService.getById(id, actorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Update conversation title")
    @PatchMapping("/{id}/title")
    public ResponseEntity<ApiResponse<AiConversationResponse>> updateTitle(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateConversationTitleRequest request,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        UpdateConversationTitleCommand command = new UpdateConversationTitleCommand(id, actorId, request.title());
        AiConversationResponse response = updateTitleAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Soft-delete a conversation")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        DeleteConversationCommand command = new DeleteConversationCommand(id, actorId);
        deleteAction.execute(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Archive a conversation")
    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<AiConversationResponse>> archiveConversation(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Actor-Id", required = false) UUID actorId) {

        ArchiveConversationCommand command = new ArchiveConversationCommand(id, actorId);
        AiConversationResponse response = archiveAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
