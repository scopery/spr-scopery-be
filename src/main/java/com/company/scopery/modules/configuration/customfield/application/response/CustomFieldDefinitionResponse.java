package com.company.scopery.modules.configuration.customfield.application.response;

import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Custom field definition details")
public record CustomFieldDefinitionResponse(
        @Schema(description = "Unique identifier of the custom field definition", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Code of the object type this field belongs to", example = "PROJECT")
        String objectTypeCode,

        @Schema(description = "Unique key identifying the field within its object type", example = "priority_level")
        String fieldKey,

        @Schema(description = "Display label for the field", example = "Priority Level")
        String label,

        @Schema(description = "Data type of the field",
                allowableValues = {"TEXT", "LONG_TEXT", "NUMBER", "DECIMAL", "CURRENCY", "DATE", "DATETIME",
                        "BOOLEAN", "SELECT", "MULTI_SELECT", "USER", "TEAM", "EXTERNAL_CONTACT",
                        "EXTERNAL_ORGANIZATION", "PROJECT", "TASK", "DOCUMENT", "URL", "EMAIL", "PHONE", "PERCENTAGE"},
                example = "TEXT")
        String fieldType,

        @Schema(description = "Whether a value for this field is required", example = "false")
        boolean required,

        @Schema(description = "Whether the field contains sensitive data", example = "false")
        boolean sensitive,

        @Schema(description = "Whether the field is visible to clients", example = "true")
        boolean clientVisible,

        @Schema(description = "Current status of the custom field definition",
                allowableValues = {"DRAFT", "ACTIVE", "ARCHIVED"},
                example = "ACTIVE")
        String status) {

    public static CustomFieldDefinitionResponse from(CustomFieldDefinition d) {
        return new CustomFieldDefinitionResponse(d.id(), d.objectTypeCode(), d.fieldKey(), d.label(), d.fieldType().name(), d.required(), d.sensitive(), d.clientVisible(), d.status().name());
    }
}
