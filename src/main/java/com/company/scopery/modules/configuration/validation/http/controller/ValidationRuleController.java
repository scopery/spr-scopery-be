package com.company.scopery.modules.configuration.validation.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import com.company.scopery.modules.configuration.validation.application.action.*;
import com.company.scopery.modules.configuration.validation.application.command.*;
import com.company.scopery.modules.configuration.validation.application.response.CustomFieldValidationRuleResponse;
import com.company.scopery.modules.configuration.validation.application.service.ValidationRuleQueryService;
import com.company.scopery.modules.configuration.validation.http.request.CreateValidationRuleRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Configuration - Field Validation")
public class ValidationRuleController {
    private final CreateValidationRuleAction create; private final DeleteValidationRuleAction delete; private final ValidationRuleQueryService query;
    public ValidationRuleController(CreateValidationRuleAction create, DeleteValidationRuleAction delete, ValidationRuleQueryService query) {
        this.create=create; this.delete=delete; this.query=query;
    }
    @PostMapping(ConfigurationApiPaths.CUSTOM_FIELDS + "/{fieldId}/validation-rules") @Operation(summary="Create validation rule")
    public ApiResponse<CustomFieldValidationRuleResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID fieldId, @Valid @RequestBody CreateValidationRuleRequest r) {
        return ApiResponse.success(create.execute(new CreateValidationRuleCommand(workspaceId, fieldId, r.ruleType(), r.ruleConfigJson())));
    }
    @GetMapping(ConfigurationApiPaths.CUSTOM_FIELDS + "/{fieldId}/validation-rules") @Operation(summary="List validation rules")
    public ApiResponse<List<CustomFieldValidationRuleResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID fieldId) {
        return ApiResponse.success(query.list(workspaceId, fieldId));
    }
    @DeleteMapping(ConfigurationApiPaths.FIELD_VALIDATION_RULES + "/{ruleId}") @Operation(summary="Delete validation rule")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID ruleId) {
        delete.execute(new DeleteValidationRuleCommand(workspaceId, ruleId)); return ApiResponse.success(null);
    }
}
