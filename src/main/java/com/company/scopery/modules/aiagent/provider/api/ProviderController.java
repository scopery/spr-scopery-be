package com.company.scopery.modules.aiagent.provider.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.provider.api.request.*;
import com.company.scopery.modules.aiagent.provider.application.ProviderApplicationService;
import com.company.scopery.modules.aiagent.provider.application.command.*;
import com.company.scopery.modules.aiagent.provider.application.query.*;
import com.company.scopery.modules.aiagent.provider.application.response.*;
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

@Tag(name = "AI Agent - Providers", description = "Manage AI provider configurations")
@RestController
@RequestMapping(AiAgentApiPaths.PROVIDERS)
public class ProviderController {

    private final ProviderApplicationService providerApplicationService;

    public ProviderController(ProviderApplicationService providerApplicationService) {
        this.providerApplicationService = providerApplicationService;
    }

    @Operation(summary = "Create a new AI provider")
    @PostMapping
    public ResponseEntity<ApiResponse<ProviderResponse>> createProvider(
            @Valid @RequestBody CreateProviderRequest request) {

        CreateProviderCommand command = new CreateProviderCommand(
                request.name(), request.code(), request.type(),
                request.apiBaseUrl(), request.description());

        ProviderResponse response = providerApplicationService.createProvider(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing provider")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderDetailResponse>> updateProvider(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProviderRequest request) {

        UpdateProviderCommand command = new UpdateProviderCommand(
                id, request.name(), request.type(), request.apiBaseUrl(), request.description());

        ProviderDetailResponse response = providerApplicationService.updateProvider(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get provider detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderDetailResponse>> getProviderDetail(@PathVariable UUID id) {
        GetProviderDetailQuery query = new GetProviderDetailQuery(id);
        ProviderDetailResponse response = providerApplicationService.getProviderDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list providers with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProviderResponse>>> searchProviders(
            @Parameter(description = "Search by name or code") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by type (e.g. LLM, OCR, EMBEDDING)") @RequestParam(required = false) String type,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)") @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchProviderQuery query = new SearchProviderQuery(keyword, type, status, page, size);
        Page<ProviderResponse> result = providerApplicationService.searchProviders(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate a provider")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<ProviderDetailResponse>> activateProvider(@PathVariable UUID id) {
        ActivateProviderCommand command = new ActivateProviderCommand(id);
        ProviderDetailResponse response = providerApplicationService.activateProvider(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate a provider")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ProviderDetailResponse>> deactivateProvider(@PathVariable UUID id) {
        DeactivateProviderCommand command = new DeactivateProviderCommand(id);
        ProviderDetailResponse response = providerApplicationService.deactivateProvider(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
