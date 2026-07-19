package com.company.scopery.modules.configuration.form.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Schema(description = "Request payload to add a field to a form version")
public record CreateFormFieldRequest(
        @Schema(description = "Source type of the field",
                allowableValues = {"CORE_FIELD", "CUSTOM_FIELD", "READONLY_DISPLAY", "INSTRUCTION_TEXT", "SEPARATOR"},
                example = "CUSTOM_FIELD")
        @NotBlank String fieldSource,

        @Schema(description = "Identifier of the section to place the field in", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID sectionId,

        @Schema(description = "Identifier of the custom field definition (when fieldSource is CUSTOM_FIELD)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID customFieldDefinitionId,

        @Schema(description = "Key of the core field (when fieldSource is CORE_FIELD)", example = "name", nullable = true)
        String coreFieldKey,

        @Schema(description = "Whether the field is required on this form", example = "false", nullable = true)
        Boolean requiredOnForm,

        @Schema(description = "Whether the field should be displayed as read-only", example = "false", nullable = true)
        Boolean readonlyFlag,

        @Schema(description = "Sort order position of the field within its section", example = "1", nullable = true)
        Integer sortOrder) {}
