package com.company.scopery.modules.aiagent.prompt.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.prompt.api.request.*;
import com.company.scopery.modules.aiagent.prompt.application.PromptVersionApplicationService;
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

@Tag(name = "AI Agent - Prompt Versions",
     description = "Manage versioned prompt content under prompt templates")
@RestController
@RequestMapping(AiAgentApiPaths.PROMPT_VERSIONS)
public class PromptVersionController {

    private final PromptVersionApplicationService versionApplicationService;

    public PromptVersionController(PromptVersionApplicationService versionApplicationService) {
        this.versionApplicationService = versionApplicationService;
    }

    @Operation(summary = "Create a new prompt version under a template")
    @PostMapping
    public ResponseEntity<ApiResponse<PromptVersionResponse>> createPromptVersion(
            @Valid @RequestBody CreatePromptVersionRequest request) {

        CreatePromptVersionCommand command = new CreatePromptVersionCommand(
                request.templateId(), request.title(), request.content(),
                request.contentFormat(), request.variableSchema(), request.changeNote());

        PromptVersionResponse response = versionApplicationService.createPromptVersion(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update a prompt version (only allowed while DRAFT)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> updatePromptVersion(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePromptVersionRequest request) {

        UpdatePromptVersionCommand command = new UpdatePromptVersionCommand(
                id, request.title(), request.content(), request.contentFormat(),
                request.variableSchema(), request.changeNote());

        PromptVersionDetailResponse response = versionApplicationService.updatePromptVersion(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get prompt version detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> getPromptVersionDetail(
            @PathVariable UUID id) {

        GetPromptVersionDetailQuery query = new GetPromptVersionDetailQuery(id);
        PromptVersionDetailResponse response = versionApplicationService.getPromptVersionDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
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

        SearchPromptVersionQuery query = new SearchPromptVersionQuery(
                templateId, status, contentFormat, page, size);
        Page<PromptVersionResponse> result = versionApplicationService.searchPromptVersions(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate a prompt version (archives current active version under same template)")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> activatePromptVersion(
            @PathVariable UUID id) {

        ActivatePromptVersionCommand command = new ActivatePromptVersionCommand(id);
        PromptVersionDetailResponse response = versionApplicationService.activatePromptVersion(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Archive a prompt version")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<PromptVersionDetailResponse>> archivePromptVersion(
            @PathVariable UUID id) {

        ArchivePromptVersionCommand command = new ArchivePromptVersionCommand(id);
        PromptVersionDetailResponse response = versionApplicationService.archivePromptVersion(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
