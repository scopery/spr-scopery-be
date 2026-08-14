package com.company.scopery.modules.traceability.validationruletype.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.validationruletype.application.action.CreateRegistryValidationRuleTypeAction;
import com.company.scopery.modules.traceability.validationruletype.application.action.DeleteRegistryValidationRuleTypeAction;
import com.company.scopery.modules.traceability.validationruletype.application.action.UpdateRegistryValidationRuleTypeAction;
import com.company.scopery.modules.traceability.validationruletype.application.command.CreateRegistryValidationRuleTypeCommand;
import com.company.scopery.modules.traceability.validationruletype.application.command.UpdateRegistryValidationRuleTypeCommand;
import com.company.scopery.modules.traceability.validationruletype.application.response.RegistryValidationRuleTypeResponse;
import com.company.scopery.modules.traceability.validationruletype.application.service.RegistryValidationRuleTypeQueryService;
import com.company.scopery.modules.traceability.validationruletype.http.request.CreateRegistryValidationRuleTypeRequest;
import com.company.scopery.modules.traceability.validationruletype.http.request.UpdateRegistryValidationRuleTypeRequest;
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
@RequestMapping(TraceabilityApiPaths.VALIDATION_RULE_TYPES)
@Tag(name = "Traceability - Validation Rule Types")
public class RegistryValidationRuleTypeController {

    private final CreateRegistryValidationRuleTypeAction create;
    private final UpdateRegistryValidationRuleTypeAction update;
    private final DeleteRegistryValidationRuleTypeAction delete;
    private final RegistryValidationRuleTypeQueryService query;

    public RegistryValidationRuleTypeController(CreateRegistryValidationRuleTypeAction create,
                                                 UpdateRegistryValidationRuleTypeAction update,
                                                 DeleteRegistryValidationRuleTypeAction delete,
                                                 RegistryValidationRuleTypeQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create workspace validation rule type")
    public ApiResponse<RegistryValidationRuleTypeResponse> create(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateRegistryValidationRuleTypeRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryValidationRuleTypeCommand(
                workspaceId, r.code(), r.name(), r.category(),
                r.paramSchemaJson(), r.defaultMessage(), r.description(), r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List validation rule types (system + workspace)")
    public ApiResponse<List<RegistryValidationRuleTypeResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listAccessible(workspaceId));
    }

    @GetMapping("/{ruleTypeId}")
    @Operation(summary = "Get validation rule type by ID")
    public ApiResponse<RegistryValidationRuleTypeResponse> get(
            @PathVariable UUID workspaceId,
            @PathVariable UUID ruleTypeId) {
        return ApiResponse.success(query.get(workspaceId, ruleTypeId));
    }

    @PutMapping("/{ruleTypeId}")
    @Operation(summary = "Update workspace validation rule type")
    public ApiResponse<RegistryValidationRuleTypeResponse> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID ruleTypeId,
            @Valid @RequestBody UpdateRegistryValidationRuleTypeRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryValidationRuleTypeCommand(
                workspaceId, ruleTypeId, r.name(), r.category(),
                r.paramSchemaJson(), r.defaultMessage(), r.description(), r.displayOrder())));
    }

    @DeleteMapping("/{ruleTypeId}")
    @Operation(summary = "Delete workspace validation rule type")
    public ApiResponse<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID ruleTypeId) {
        delete.execute(workspaceId, ruleTypeId);
        return ApiResponse.success(null);
    }
}
