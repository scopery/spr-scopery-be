package com.company.scopery.modules.aiagent.prompt.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.prompt.application.action.ActivatePromptTemplateAction;
import com.company.scopery.modules.aiagent.prompt.application.action.CreatePromptTemplateAction;
import com.company.scopery.modules.aiagent.prompt.application.action.DeactivatePromptTemplateAction;
import com.company.scopery.modules.aiagent.prompt.application.action.UpdatePromptTemplateAction;
import com.company.scopery.modules.aiagent.prompt.application.command.ActivatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.CreatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.DeactivatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.UpdatePromptTemplateCommand;
import com.company.scopery.modules.aiagent.prompt.application.query.GetPromptTemplateDetailQuery;
import com.company.scopery.modules.aiagent.prompt.application.query.SearchPromptTemplateQuery;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateDetailResponse;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptTemplateResponse;
import com.company.scopery.modules.aiagent.prompt.application.service.PromptTemplateQueryService;
import com.company.scopery.modules.aiagent.prompt.http.request.CreatePromptTemplateRequest;
import com.company.scopery.modules.aiagent.prompt.http.request.UpdatePromptTemplateRequest;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Prompt Templates",
     description = "Manage reusable prompt template identities linked to agents")
@RestController
@RequestMapping(AiAgentApiPaths.PROMPT_TEMPLATES)
public class PromptTemplateController {

    private final CreatePromptTemplateAction createAction;
    private final UpdatePromptTemplateAction updateAction;
    private final ActivatePromptTemplateAction activateAction;
    private final DeactivatePromptTemplateAction deactivateAction;
    private final PromptTemplateQueryService queryService;

    public PromptTemplateController(CreatePromptTemplateAction createAction,
                                    UpdatePromptTemplateAction updateAction,
                                    ActivatePromptTemplateAction activateAction,
                                    DeactivatePromptTemplateAction deactivateAction,
                                    PromptTemplateQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new prompt template")
    @PostMapping
    public ResponseEntity<ApiResponse<PromptTemplateResponse>> createPromptTemplate(
            @Valid @RequestBody CreatePromptTemplateRequest request) {

        CreatePromptTemplateCommand command = new CreatePromptTemplateCommand(
                request.agentId(), request.name(), request.code(), request.description());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createAction.execute(command)));
    }

    @Operation(summary = "Update an existing prompt template")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> updatePromptTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePromptTemplateRequest request) {

        UpdatePromptTemplateCommand command = new UpdatePromptTemplateCommand(
                id, request.name(), request.description());

        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(command)));
    }

    @Operation(summary = "Get prompt template detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> getPromptTemplateDetail(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                queryService.getPromptTemplateDetail(new GetPromptTemplateDetailQuery(id))));
    }

    @Operation(summary = "Search and list prompt templates with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PromptTemplateResponse>>> searchPromptTemplates(
            @Parameter(description = "Filter by agent ID") @RequestParam(required = false) UUID agentId,
            @Parameter(description = "Filter by name or code (partial, case-insensitive)")
                @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)")
                @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResult<PromptTemplateResponse> result = queryService.searchPromptTemplates(
                new SearchPromptTemplateQuery(agentId, keyword, status, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate a prompt template")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> activatePromptTemplate(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                activateAction.execute(new ActivatePromptTemplateCommand(id))));
    }

    @Operation(summary = "Deactivate a prompt template")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> deactivatePromptTemplate(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                deactivateAction.execute(new DeactivatePromptTemplateCommand(id))));
    }
}
