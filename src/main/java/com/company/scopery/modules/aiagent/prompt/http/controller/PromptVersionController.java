package com.company.scopery.modules.aiagent.prompt.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.prompt.application.action.ActivatePromptVersionAction;
import com.company.scopery.modules.aiagent.prompt.application.action.ArchivePromptVersionAction;
import com.company.scopery.modules.aiagent.prompt.application.action.CreatePromptVersionAction;
import com.company.scopery.modules.aiagent.prompt.application.action.UpdatePromptVersionAction;
import com.company.scopery.modules.aiagent.prompt.application.command.ActivatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.ArchivePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.CreatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.command.UpdatePromptVersionCommand;
import com.company.scopery.modules.aiagent.prompt.application.query.GetPromptVersionDetailQuery;
import com.company.scopery.modules.aiagent.prompt.application.query.SearchPromptVersionQuery;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionDetailResponse;
import com.company.scopery.modules.aiagent.prompt.application.response.PromptVersionResponse;
import com.company.scopery.modules.aiagent.prompt.application.service.PromptVersionQueryService;
import com.company.scopery.modules.aiagent.prompt.http.request.CreatePromptVersionRequest;
import com.company.scopery.modules.aiagent.prompt.http.request.UpdatePromptVersionRequest;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Prompt Versions",
     description = "Manage versioned prompt content under prompt templates")
@RestController
@RequestMapping(AiAgentApiPaths.PROMPT_VERSIONS)
public class PromptVersionController {

    private final CreatePromptVersionAction createAction;
    private final UpdatePromptVersionAction updateAction;
    private final ActivatePromptVersionAction activateAction;
    private final ArchivePromptVersionAction archiveAction;
    private final PromptVersionQueryService queryService;

    public PromptVersionController(CreatePromptVersionAction createAction,
                                   UpdatePromptVersionAction updateAction,
                                   ActivatePromptVersionAction activateAction,
                                   ArchivePromptVersionAction archiveAction,
                                   PromptVersionQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.archiveAction = archiveAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new prompt version under a template")
    @PostMapping
    public ResponseEntity<ApiResponse<PromptVersionResponse>> createPromptVersion(
            @Valid @RequestBody CreatePromptVersionRequest request) {

        CreatePromptVersionCommand command = new CreatePromptVersionCommand(
                request.templateId(), request.title(), request.content(),
                request.contentFormat(), request.variableSchema(), request.changeNote());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createAction.execute(command)));
    }

    @Operation(summary = "Update a prompt version (only allowed while DRAFT)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> updatePromptVersion(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePromptVersionRequest request) {

        UpdatePromptVersionCommand command = new UpdatePromptVersionCommand(
                id, request.title(), request.content(), request.contentFormat(),
                request.variableSchema(), request.changeNote());

        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(command)));
    }

    @Operation(summary = "Get prompt version detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> getPromptVersionDetail(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                queryService.getPromptVersionDetail(new GetPromptVersionDetailQuery(id))));
    }

    @Operation(summary = "Search and list prompt versions with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PromptVersionResponse>>> searchPromptVersions(
            @Parameter(description = "Filter by template ID") @RequestParam(required = false) UUID templateId,
            @Parameter(description = "Filter by status (DRAFT, ACTIVE, ARCHIVED)")
                @RequestParam(required = false) String status,
            @Parameter(description = "Filter by content format (TEXT, MARKDOWN, JSON)")
                @RequestParam(required = false) String contentFormat,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResult<PromptVersionResponse> result = queryService.searchPromptVersions(
                new SearchPromptVersionQuery(templateId, status, contentFormat, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate a prompt version (archives current active version under same template)")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> activatePromptVersion(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                activateAction.execute(new ActivatePromptVersionCommand(id))));
    }

    @Operation(summary = "Archive a prompt version")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> archivePromptVersion(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                archiveAction.execute(new ArchivePromptVersionCommand(id))));
    }
}
