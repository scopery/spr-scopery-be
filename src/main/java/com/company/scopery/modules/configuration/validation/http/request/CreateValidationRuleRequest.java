package com.company.scopery.modules.configuration.validation.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to add a validation rule to a custom field definition")
public record CreateValidationRuleRequest(
        @Schema(description = "Type of validation rule",
                allowableValues = {"MIN_LENGTH", "MAX_LENGTH", "MIN_VALUE", "MAX_VALUE", "REGEX", "REQUIRED", "UNIQUE", "ALLOWED_VALUES", "DATE_RANGE"},
                example = "MAX_LENGTH")
        @NotBlank String ruleType,

        @Schema(description = "JSON configuration for the rule parameters", example = "{\"max\":255}", nullable = true)
        String ruleConfigJson) {}
