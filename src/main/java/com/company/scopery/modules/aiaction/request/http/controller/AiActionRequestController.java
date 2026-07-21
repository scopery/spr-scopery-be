package com.company.scopery.modules.aiaction.request.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiaction.application.action.CreateAiActionRequestAction;
import com.company.scopery.modules.aiaction.application.command.CreateAiActionRequestCommand;
import com.company.scopery.modules.aiaction.application.response.AiActionRequestResponse;
import com.company.scopery.modules.aiaction.application.service.AiActionRequestQueryService;
import com.company.scopery.modules.aiaction.request.domain.enums.AiActionOriginType;
import com.company.scopery.modules.aiaction.shared.constant.AiActionApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(AiActionApiPaths.REQUESTS)
@Tag(name = "AI Action - Requests")
public class AiActionRequestController {

    private final CreateAiActionRequestAction createRequestAction;
    private final AiActionRequestQueryService requestQueryService;

    public AiActionRequestController(CreateAiActionRequestAction createRequestAction,
                                     AiActionRequestQueryService requestQueryService) {
        this.createRequestAction = createRequestAction;
        this.requestQueryService = requestQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new AI action request")
    public ResponseEntity<ApiResponse<AiActionRequestResponse>> createRequest(
            @RequestBody Map<String, Object> body) {

        UUID workspaceId = UUID.fromString(String.valueOf(body.getOrDefault("workspaceId", UUID.randomUUID())));
        UUID projectId = body.containsKey("projectId")
                ? UUID.fromString(String.valueOf(body.get("projectId")))
                : null;
        UUID actorId = UUID.fromString(String.valueOf(body.getOrDefault("actorId", UUID.randomUUID())));
        String originType = String.valueOf(body.getOrDefault("originType", "DIRECT_CHAT"));
        String intentSummary = String.valueOf(body.getOrDefault("intentSummary", ""));
        String idempotencyKey = String.valueOf(body.getOrDefault("idempotencyKey", UUID.randomUUID().toString()));

        CreateAiActionRequestCommand command = new CreateAiActionRequestCommand(
                workspaceId, projectId, actorId,
                AiActionOriginType.valueOf(originType),
                null, null, null, null, null,
                intentSummary, List.of(), idempotencyKey);

        AiActionRequestResponse response = createRequestAction.execute(command);
        return ResponseEntity.status(202).body(ApiResponse.success(response));
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Get an AI action request by ID")
    public ResponseEntity<ApiResponse<AiActionRequestResponse>> getRequest(
            @PathVariable UUID requestId) {

        AiActionRequestResponse response = requestQueryService.getRequest(requestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
