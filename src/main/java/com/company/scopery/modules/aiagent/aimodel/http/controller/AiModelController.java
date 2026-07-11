package com.company.scopery.modules.aiagent.aimodel.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.aimodel.application.action.ActivateAiModelAction;
import com.company.scopery.modules.aiagent.aimodel.application.action.CreateAiModelAction;
import com.company.scopery.modules.aiagent.aimodel.application.action.DeactivateAiModelAction;
import com.company.scopery.modules.aiagent.aimodel.application.action.UpdateAiModelAction;
import com.company.scopery.modules.aiagent.aimodel.application.command.ActivateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.command.CreateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.command.DeactivateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.command.UpdateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.query.GetAiModelDetailQuery;
import com.company.scopery.modules.aiagent.aimodel.application.query.SearchAiModelQuery;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelDetailResponse;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelResponse;
import com.company.scopery.modules.aiagent.aimodel.application.service.AiModelQueryService;
import com.company.scopery.modules.aiagent.aimodel.http.request.CreateAiModelRequest;
import com.company.scopery.modules.aiagent.aimodel.http.request.UpdateAiModelRequest;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Models", description = "Manage AI model catalog under providers")
@RestController
@RequestMapping(AiAgentApiPaths.MODELS)
public class AiModelController {

    private final AiModelQueryService queryService;
    private final CreateAiModelAction createAction;
    private final UpdateAiModelAction updateAction;
    private final ActivateAiModelAction activateAction;
    private final DeactivateAiModelAction deactivateAction;

    public AiModelController(AiModelQueryService queryService,
                              CreateAiModelAction createAction,
                              UpdateAiModelAction updateAction,
                              ActivateAiModelAction activateAction,
                              DeactivateAiModelAction deactivateAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
    }

    @Operation(summary = "Create a new AI model")
    @PostMapping
    public ResponseEntity<ApiResponse<AiModelResponse>> createAiModel(
            @Valid @RequestBody CreateAiModelRequest request) {

        CreateAiModelCommand command = new CreateAiModelCommand(
                request.providerId(), request.name(), request.code(),
                request.providerModelId(), request.type(), request.description());

        AiModelResponse response = createAction.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing AI model")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AiModelDetailResponse>> updateAiModel(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAiModelRequest request) {

        UpdateAiModelCommand command = new UpdateAiModelCommand(
                id, request.name(), request.providerModelId(), request.type(), request.description());

        AiModelDetailResponse response = updateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get AI model detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AiModelDetailResponse>> getAiModelDetail(@PathVariable UUID id) {
        GetAiModelDetailQuery query = new GetAiModelDetailQuery(id);
        AiModelDetailResponse response = queryService.getAiModelDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list AI models with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AiModelResponse>>> searchAiModels(
            @Parameter(description = "Filter by provider ID") @RequestParam(required = false) UUID providerId,
            @Parameter(description = "Search by name, code, or provider model ID") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by type (CHAT, EMBEDDING, IMAGE, OCR, RERANKING, INTERNAL)") @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchAiModelQuery query = new SearchAiModelQuery(providerId, keyword, status, type, page, size);
        PageResult<AiModelResponse> result = queryService.searchAiModels(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate an AI model")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AiModelDetailResponse>> activateAiModel(@PathVariable UUID id) {
        ActivateAiModelCommand command = new ActivateAiModelCommand(id);
        AiModelDetailResponse response = activateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate an AI model")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AiModelDetailResponse>> deactivateAiModel(@PathVariable UUID id) {
        DeactivateAiModelCommand command = new DeactivateAiModelCommand(id);
        AiModelDetailResponse response = deactivateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
