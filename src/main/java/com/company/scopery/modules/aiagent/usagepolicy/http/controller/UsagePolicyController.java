package com.company.scopery.modules.aiagent.usagepolicy.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import com.company.scopery.modules.aiagent.usagepolicy.application.action.ActivateUsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.application.action.CreateUsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.application.action.DeactivateUsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.application.action.UpdateUsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.*;
import com.company.scopery.modules.aiagent.usagepolicy.application.query.*;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.*;
import com.company.scopery.modules.aiagent.usagepolicy.application.service.UsagePolicyQueryService;
import com.company.scopery.modules.aiagent.usagepolicy.http.request.CreateUsagePolicyRequest;
import com.company.scopery.modules.aiagent.usagepolicy.http.request.UpdateUsagePolicyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Usage Policies", description = "Manage AI usage rate limits and budget controls")
@RestController
@RequestMapping(AiAgentApiPaths.USAGE_POLICIES)
public class UsagePolicyController {

    private final CreateUsagePolicyAction createAction;
    private final UpdateUsagePolicyAction updateAction;
    private final ActivateUsagePolicyAction activateAction;
    private final DeactivateUsagePolicyAction deactivateAction;
    private final UsagePolicyQueryService queryService;

    public UsagePolicyController(CreateUsagePolicyAction createAction,
                                  UpdateUsagePolicyAction updateAction,
                                  ActivateUsagePolicyAction activateAction,
                                  DeactivateUsagePolicyAction deactivateAction,
                                  UsagePolicyQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.queryService = queryService;
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

        UsagePolicyResponse response = createAction.execute(command);
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

        UsagePolicyDetailResponse response = updateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get usage policy detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsagePolicyDetailResponse>> getUsagePolicyDetail(
            @PathVariable UUID id) {

        GetUsagePolicyDetailQuery query = new GetUsagePolicyDetailQuery(id);
        UsagePolicyDetailResponse response = queryService.getUsagePolicyDetail(query);
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
        PageResult<UsagePolicyResponse> result = queryService.searchUsagePolicies(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate a usage policy")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UsagePolicyDetailResponse>> activateUsagePolicy(
            @PathVariable UUID id) {

        ActivateUsagePolicyCommand command = new ActivateUsagePolicyCommand(id);
        UsagePolicyDetailResponse response = activateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate a usage policy")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UsagePolicyDetailResponse>> deactivateUsagePolicy(
            @PathVariable UUID id) {

        DeactivateUsagePolicyCommand command = new DeactivateUsagePolicyCommand(id);
        UsagePolicyDetailResponse response = deactivateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
