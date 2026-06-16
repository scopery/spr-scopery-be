package com.company.scopery.modules.aiagent.deployment.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.deployment.api.request.*;
import com.company.scopery.modules.aiagent.deployment.application.ModelDeploymentApplicationService;
import com.company.scopery.modules.aiagent.deployment.application.command.*;
import com.company.scopery.modules.aiagent.deployment.application.query.*;
import com.company.scopery.modules.aiagent.deployment.application.response.*;
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

@Tag(name = "AI Agent - Model Deployments", description = "Manage runtime model deployment configurations")
@RestController
@RequestMapping(AiAgentApiPaths.MODEL_DEPLOYMENTS)
public class ModelDeploymentController {

    private final ModelDeploymentApplicationService deploymentApplicationService;

    public ModelDeploymentController(ModelDeploymentApplicationService deploymentApplicationService) {
        this.deploymentApplicationService = deploymentApplicationService;
    }

    @Operation(summary = "Create a new model deployment")
    @PostMapping
    public ResponseEntity<ApiResponse<ModelDeploymentResponse>> createModelDeployment(
            @Valid @RequestBody CreateModelDeploymentRequest request) {

        CreateModelDeploymentCommand command = new CreateModelDeploymentCommand(
                request.modelId(), request.name(), request.code(), request.environment(),
                request.providerDeploymentId(), request.endpointUrl(),
                request.defaultTemperature(), request.defaultMaxOutputTokens(),
                Boolean.TRUE.equals(request.isDefault()), request.description());

        ModelDeploymentResponse response = deploymentApplicationService.createModelDeployment(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing model deployment")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelDeploymentDetailResponse>> updateModelDeployment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateModelDeploymentRequest request) {

        UpdateModelDeploymentCommand command = new UpdateModelDeploymentCommand(
                id, request.name(), request.providerDeploymentId(), request.endpointUrl(),
                request.defaultTemperature(), request.defaultMaxOutputTokens(),
                Boolean.TRUE.equals(request.isDefault()), request.description());

        ModelDeploymentDetailResponse response = deploymentApplicationService.updateModelDeployment(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get model deployment detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelDeploymentDetailResponse>> getModelDeploymentDetail(
            @PathVariable UUID id) {
        GetModelDeploymentDetailQuery query = new GetModelDeploymentDetailQuery(id);
        ModelDeploymentDetailResponse response = deploymentApplicationService.getModelDeploymentDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list model deployments with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ModelDeploymentResponse>>> searchModelDeployments(
            @Parameter(description = "Filter by AI model ID") @RequestParam(required = false) UUID modelId,
            @Parameter(description = "Filter by environment (DEV, UAT, PROD)") @RequestParam(required = false) String environment,
            @Parameter(description = "Search by name, code, or provider deployment ID") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE, DEPRECATED)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter default deployments only") @RequestParam(required = false) Boolean isDefault,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchModelDeploymentQuery query = new SearchModelDeploymentQuery(
                modelId, environment, keyword, status, isDefault, page, size);
        Page<ModelDeploymentResponse> result = deploymentApplicationService.searchModelDeployments(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate a model deployment")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<ModelDeploymentDetailResponse>> activateModelDeployment(
            @PathVariable UUID id) {
        ActivateModelDeploymentCommand command = new ActivateModelDeploymentCommand(id);
        ModelDeploymentDetailResponse response = deploymentApplicationService.activateModelDeployment(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate a model deployment")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ModelDeploymentDetailResponse>> deactivateModelDeployment(
            @PathVariable UUID id) {
        DeactivateModelDeploymentCommand command = new DeactivateModelDeploymentCommand(id);
        ModelDeploymentDetailResponse response = deploymentApplicationService.deactivateModelDeployment(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Set a model deployment as the default for its model and environment")
    @PatchMapping("/{id}/set-default")
    public ResponseEntity<ApiResponse<ModelDeploymentDetailResponse>> setDefaultModelDeployment(
            @PathVariable UUID id) {
        SetDefaultModelDeploymentCommand command = new SetDefaultModelDeploymentCommand(id);
        ModelDeploymentDetailResponse response = deploymentApplicationService.setDefaultModelDeployment(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
