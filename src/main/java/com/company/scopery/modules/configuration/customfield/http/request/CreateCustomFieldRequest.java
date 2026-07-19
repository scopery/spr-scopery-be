package com.company.scopery.modules.configuration.customfield.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to create a new custom field definition")
public record CreateCustomFieldRequest(
        @Schema(description = "Code of the object type this field belongs to", example = "PROJECT")
        @NotBlank String objectTypeCode,

        @Schema(description = "Unique key identifying the field within its object type", example = "priority_level")
        @NotBlank String fieldKey,

        @Schema(description = "Display label for the field", example = "Priority Level")
        @NotBlank String label,

        @Schema(description = "Data type of the field",
                allowableValues = {"TEXT", "LONG_TEXT", "NUMBER", "DECIMAL", "CURRENCY", "DATE", "DATETIME",
                        "BOOLEAN", "SELECT", "MULTI_SELECT", "USER", "TEAM", "EXTERNAL_CONTACT",
                        "EXTERNAL_ORGANIZATION", "PROJECT", "TASK", "DOCUMENT", "URL", "EMAIL", "PHONE", "PERCENTAGE"},
                example = "TEXT")
        @NotBlank String fieldType,

        @Schema(description = "Whether a value for this field is required", example = "false", nullable = true)
        Boolean required,

        @Schema(description = "Whether the field contains sensitive data", example = "false", nullable = true)
        Boolean sensitive,

        @Schema(description = "Whether the field is visible to clients", example = "true", nullable = true)
        Boolean clientVisible) {}
