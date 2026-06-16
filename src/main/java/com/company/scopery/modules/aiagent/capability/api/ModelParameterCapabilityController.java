package com.company.scopery.modules.aiagent.capability.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.capability.api.request.*;
import com.company.scopery.modules.aiagent.capability.application.ModelParameterCapabilityApplicationService;
import com.company.scopery.modules.aiagent.capability.application.command.*;
import com.company.scopery.modules.aiagent.capability.application.query.*;
import com.company.scopery.modules.aiagent.capability.application.response.*;
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

@Tag(name = "AI Agent - Model Parameter Capabilities",
     description = "Manage parameter support and validation rules for AI models")
@RestController
@RequestMapping(AiAgentApiPaths.MODEL_PARAMETER_CAPABILITIES)
public class ModelParameterCapabilityController {

    private final ModelParameterCapabilityApplicationService capabilityApplicationService;

    public ModelParameterCapabilityController(
            ModelParameterCapabilityApplicationService capabilityApplicationService) {
        this.capabilityApplicationService = capabilityApplicationService;
    }

    @Operation(summary = "Create a new model parameter capability")
    @PostMapping
    public ResponseEntity<ApiResponse<ModelParameterCapabilityResponse>> createModelParameterCapability(
            @Valid @RequestBody CreateModelParameterCapabilityRequest request) {

        CreateModelParameterCapabilityCommand command = new CreateModelParameterCapabilityCommand(
                request.modelId(), request.parameterName(), request.apiParameterKey(),
                request.supportStatus(), request.valueType(),
                request.minValue(), request.maxValue(), request.defaultValue(),
                Boolean.TRUE.equals(request.nullable()), request.ifNullBehavior(),
                request.description());

        ModelParameterCapabilityResponse response =
                capabilityApplicationService.createModelParameterCapability(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update an existing model parameter capability")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelParameterCapabilityDetailResponse>> updateModelParameterCapability(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateModelParameterCapabilityRequest request) {

        UpdateModelParameterCapabilityCommand command = new UpdateModelParameterCapabilityCommand(
                id, request.apiParameterKey(), request.supportStatus(), request.valueType(),
                request.minValue(), request.maxValue(), request.defaultValue(),
                Boolean.TRUE.equals(request.nullable()), request.ifNullBehavior(),
                request.description());

        ModelParameterCapabilityDetailResponse response =
                capabilityApplicationService.updateModelParameterCapability(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get model parameter capability detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModelParameterCapabilityDetailResponse>> getModelParameterCapabilityDetail(
            @PathVariable UUID id) {

        GetModelParameterCapabilityDetailQuery query = new GetModelParameterCapabilityDetailQuery(id);
        ModelParameterCapabilityDetailResponse response =
                capabilityApplicationService.getModelParameterCapabilityDetail(query);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Search and list model parameter capabilities with optional filters")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ModelParameterCapabilityResponse>>> searchModelParameterCapabilities(
            @Parameter(description = "Filter by AI model ID") @RequestParam(required = false) UUID modelId,
            @Parameter(description = "Filter by parameter name (partial, case-insensitive)")
                @RequestParam(required = false) String parameterName,
            @Parameter(description = "Filter by support status (YES, NO, CONDITIONAL)")
                @RequestParam(required = false) String supportStatus,
            @Parameter(description = "Filter by value type (NUMBER, INTEGER, STRING, BOOLEAN)")
                @RequestParam(required = false) String valueType,
            @Parameter(description = "Filter by status (ACTIVE, INACTIVE)")
                @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchModelParameterCapabilityQuery query = new SearchModelParameterCapabilityQuery(
                modelId, parameterName, supportStatus, valueType, status, page, size);
        Page<ModelParameterCapabilityResponse> result =
                capabilityApplicationService.searchModelParameterCapabilities(query);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @Operation(summary = "Activate a model parameter capability")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<ModelParameterCapabilityDetailResponse>> activateModelParameterCapability(
            @PathVariable UUID id) {

        ActivateModelParameterCapabilityCommand command = new ActivateModelParameterCapabilityCommand(id);
        ModelParameterCapabilityDetailResponse response =
                capabilityApplicationService.activateModelParameterCapability(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Deactivate a model parameter capability")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ModelParameterCapabilityDetailResponse>> deactivateModelParameterCapability(
            @PathVariable UUID id) {

        DeactivateModelParameterCapabilityCommand command = new DeactivateModelParameterCapabilityCommand(id);
        ModelParameterCapabilityDetailResponse response =
                capabilityApplicationService.deactivateModelParameterCapability(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
