package com.company.scopery.modules.configuration.validation.application.response;

import com.company.scopery.modules.configuration.validation.domain.model.CustomFieldValidationRule;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Custom field validation rule details")
public record CustomFieldValidationRuleResponse(
        @Schema(description = "Unique identifier of the validation rule", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the custom field definition this rule applies to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID customFieldDefinitionId,

        @Schema(description = "Type of validation rule",
                allowableValues = {"MIN_LENGTH", "MAX_LENGTH", "MIN_VALUE", "MAX_VALUE", "REGEX", "REQUIRED", "UNIQUE", "ALLOWED_VALUES", "DATE_RANGE"},
                example = "MAX_LENGTH")
        String ruleType,

        @Schema(description = "JSON configuration for the rule parameters", example = "{\"max\":255}", nullable = true)
        String ruleConfigJson,

        @Schema(description = "Current status of the validation rule",
                allowableValues = {"ACTIVE", "ARCHIVED"},
                example = "ACTIVE")
        String status) {

    public static CustomFieldValidationRuleResponse from(CustomFieldValidationRule r) {
        return new CustomFieldValidationRuleResponse(r.id(), r.customFieldDefinitionId(), r.ruleType(), r.ruleConfigJson(), r.status());
    }
}
