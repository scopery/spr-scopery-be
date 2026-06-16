package com.company.scopery.modules.aiagent.prompt.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.prompt.api.request.*;
import com.company.scopery.modules.aiagent.prompt.application.PromptTemplateApplicationService;
import com.company.scopery.modules.aiagent.prompt.application.command.*;
import com.company.scopery.modules.aiagent.prompt.application.query.*;
import com.company.scopery.modules.aiagent.prompt.application.response.*;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Prompt Templates",
     description = "Manage reusable prompt template identities linked to agents")
@RestController
@RequestMapping(AiAgentApiPaths.PROMPT_TEMPLATES)
public class PromptTemplateController {

    private final PromptTemplateApplicationService templateApplicationService;

    public PromptTemplateController(PromptTemplateApplicationService templateApplicationService) {
        this.templateApplicationService = templateApplicationService;
    }

    @Operation(summary = "Create a new prompt template")
    @PostMapping
    public ResponseEntity<ApiResponse<PromptTemplateResponse>> createPromptTemplate(
            @Valid @RequestBody CreatePromptTemplateRequest request) {

        CreatePromptTemplateCommand command = new CreatePromptTemplateCommand(
                request.agentId(), request.name(), request.code(), request.description());

        PromptTemplateResponse response = templateApplicationService.createPromptTemplate(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing prompt template")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> updatePromptTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePromptTemplateRequest request) {

        UpdatePromptTemplateCommand command = new UpdatePromptTemplateCommand(
                id, request.name(), request.description());

        PromptTemplateDetailResponse response = templateApplicationService.updatePromptTemplate(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get prompt template detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> getPromptTemplateDetail(
            @PathVariable UUID id) {

        GetPromptTemplateDetailQuery query = new GetPromptTemplateDetailQuery(id);
        PromptTemplateDetailResponse response = templateApplicationService.getPromptTemplateDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
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

        SearchPromptTemplateQuery query = new SearchPromptTemplateQuery(agentId, keyword, status, page, size);
        Page<PromptTemplateResponse> result = templateApplicationService.searchPromptTemplates(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate a prompt template")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> activatePromptTemplate(
            @PathVariable UUID id) {

        ActivatePromptTemplateCommand command = new ActivatePromptTemplateCommand(id);
        PromptTemplateDetailResponse response = templateApplicationService.activatePromptTemplate(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate a prompt template")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<PromptTemplateDetailResponse>> deactivatePromptTemplate(
            @PathVariable UUID id) {

        DeactivatePromptTemplateCommand command = new DeactivatePromptTemplateCommand(id);
        PromptTemplateDetailResponse response = templateApplicationService.deactivatePromptTemplate(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}