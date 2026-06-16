package com.company.scopery.modules.aiagent.usagepolicy.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import com.company.scopery.modules.aiagent.usagepolicy.api.request.CreateUsagePolicyRequest;
import com.company.scopery.modules.aiagent.usagepolicy.api.request.UpdateUsagePolicyRequest;
import com.company.scopery.modules.aiagent.usagepolicy.application.UsagePolicyApplicationService;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.*;
import com.company.scopery.modules.aiagent.usagepolicy.application.query.*;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Usage Policies", description = "Manage AI usage rate limits and budget controls")
@RestController
@RequestMapping(AiAgentApiPaths.USAGE_POLICIES)
public class UsagePolicyController {

    private final UsagePolicyApplicationService service;

    public UsagePolicyController(UsagePolicyApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new usage policy")
    @PostMapping
    public ResponseEntity<ApiResponse<UsagePolicyResponse>> createUsagePolicy(
            @Valid @RequestBody CreateUsagePolicyRequest request) {

        CreateUsagePolicyCommand command = new CreateUsagePolicyCommand(
                request.code(), request.name(), request.targetType(), request.targetId(),
                request.maxRequestsPerPeriod(), request.maxTokensPerPeriod(),
                request.maxCostPerPeriod(), request.maxConcurrentRequests(), request.dailyBudget(),
                request.period(), request.action(), request.priority(), request.description());

        UsagePolicyResponse response = service.createUsagePolicy(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing usage policy")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsagePolicyDetailResponse>> updateUsagePolicy(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUsagePolicyRequest request) {

        UpdateUsagePolicyCommand command = new UpdateUsagePolicyCommand(
                id, request.name(), request.maxRequestsPerPeriod(), request.maxTokensPerPeriod(),
                request.maxCostPerPeriod(), request.maxConcurrentRequests(), request.dailyBudget(),
                request.period(), request.action(), request.priority(), request.description());

        UsagePolicyDetailResponse response = service.updateUsagePolicy(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get usage policy detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsagePolicyDetailResponse>> getUsagePolicyDetail(
            @PathVariable UUID id) {

        GetUsagePolicyDetailQuery query = new GetUsagePolicyDetailQuery(id);
        UsagePolicyDetailResponse response = service.getUsagePolicyDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list usage policies with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UsagePolicyResponse>>> searchUsagePolicies(
            @Parameter(description = "Filter by name or code (partial, case-insensitive)")
                @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by target type (GLOBAL, EVENT_CONFIG, AGENT, MODEL_DEPLOYMENT)")
                @RequestParam(required = false) String targetType,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)")
                @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchUsagePolicyQuery query = new SearchUsagePolicyQuery(keyword, targetType, status, page, size);
        Page<UsagePolicyResponse> result = service.searchUsagePolicies(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate a usage policy")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UsagePolicyDetailResponse>> activateUsagePolicy(
            @PathVariable UUID id) {

        ActivateUsagePolicyCommand command = new ActivateUsagePolicyCommand(id);
        UsagePolicyDetailResponse response = service.activateUsagePolicy(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate a usage policy")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UsagePolicyDetailResponse>> deactivateUsagePolicy(
            @PathVariable UUID id) {

        DeactivateUsagePolicyCommand command = new DeactivateUsagePolicyCommand(id);
        UsagePolicyDetailResponse response = service.deactivateUsagePolicy(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}