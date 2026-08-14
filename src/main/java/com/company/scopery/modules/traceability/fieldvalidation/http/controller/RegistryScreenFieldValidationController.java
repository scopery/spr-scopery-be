package com.company.scopery.modules.traceability.fieldvalidation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.fieldvalidation.application.action.CreateRegistryScreenFieldValidationAction;
import com.company.scopery.modules.traceability.fieldvalidation.application.action.DeleteRegistryScreenFieldValidationAction;
import com.company.scopery.modules.traceability.fieldvalidation.application.action.UpdateRegistryScreenFieldValidationAction;
import com.company.scopery.modules.traceability.fieldvalidation.application.command.CreateRegistryScreenFieldValidationCommand;
import com.company.scopery.modules.traceability.fieldvalidation.application.command.DeleteRegistryScreenFieldValidationCommand;
import com.company.scopery.modules.traceability.fieldvalidation.application.command.UpdateRegistryScreenFieldValidationCommand;
import com.company.scopery.modules.traceability.fieldvalidation.application.response.RegistryScreenFieldValidationResponse;
import com.company.scopery.modules.traceability.fieldvalidation.application.service.RegistryScreenFieldValidationQueryService;
import com.company.scopery.modules.traceability.fieldvalidation.http.request.CreateRegistryScreenFieldValidationRequest;
import com.company.scopery.modules.traceability.fieldvalidation.http.request.UpdateRegistryScreenFieldValidationRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_FIELD_VALIDATIONS)
@Tag(name = "Traceability - Screen Field Validations")
public class RegistryScreenFieldValidationController {

    private final CreateRegistryScreenFieldValidationAction create;
    private final UpdateRegistryScreenFieldValidationAction update;
    private final DeleteRegistryScreenFieldValidationAction delete;
    private final RegistryScreenFieldValidationQueryService query;
    private final ObjectMapper objectMapper;

    public RegistryScreenFieldValidationController(
            CreateRegistryScreenFieldValidationAction create,
            UpdateRegistryScreenFieldValidationAction update,
            DeleteRegistryScreenFieldValidationAction delete,
            RegistryScreenFieldValidationQueryService query,
            ObjectMapper objectMapper) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
        this.objectMapper = objectMapper;
    }

    private String toJsonString(JsonNode node) {
        if (node == null || node.isNull()) return null;
        try { return objectMapper.writeValueAsString(node); } catch (JsonProcessingException e) { return null; }
    }

    @PostMapping
    @Operation(summary = "Create a field validation rule")
    public ApiResponse<RegistryScreenFieldValidationResponse> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId,
            @PathVariable UUID fieldId,
            @Valid @RequestBody CreateRegistryScreenFieldValidationRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenFieldValidationCommand(
                workspaceId, screenId, fieldId,
                r.ruleTypeId(), r.modeId(),
                toJsonString(r.ruleParamJson()), toJsonString(r.conditionJson()),
                r.errorMessage(), r.remark(), r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List field validation rules")
    public ApiResponse<List<RegistryScreenFieldValidationResponse>> list(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fieldId) {
        return ApiResponse.success(query.listByFieldId(workspaceId, fieldId));
    }

    @GetMapping("/{validationId}")
    @Operation(summary = "Get a field validation rule by ID")
    public ApiResponse<RegistryScreenFieldValidationResponse> get(
            @PathVariable UUID workspaceId,
            @PathVariable UUID validationId) {
        return ApiResponse.success(query.getById(workspaceId, validationId));
    }

    @PutMapping("/{validationId}")
    @Operation(summary = "Update a field validation rule")
    public ApiResponse<RegistryScreenFieldValidationResponse> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId,
            @PathVariable UUID fieldId,
            @PathVariable UUID validationId,
            @Valid @RequestBody UpdateRegistryScreenFieldValidationRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryScreenFieldValidationCommand(
                workspaceId, screenId, fieldId, validationId,
                r.modeId(), toJsonString(r.ruleParamJson()), toJsonString(r.conditionJson()),
                r.errorMessage(), r.remark(), r.displayOrder())));
    }

    @DeleteMapping("/{validationId}")
    @Operation(summary = "Delete a field validation rule")
    public ApiResponse<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId,
            @PathVariable UUID fieldId,
            @PathVariable UUID validationId) {
        delete.execute(new DeleteRegistryScreenFieldValidationCommand(workspaceId, screenId, fieldId, validationId));
        return ApiResponse.success(null);
    }
}
