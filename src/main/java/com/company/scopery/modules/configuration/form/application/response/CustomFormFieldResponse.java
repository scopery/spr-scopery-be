package com.company.scopery.modules.configuration.form.application.response;

import com.company.scopery.modules.configuration.form.domain.model.CustomFormField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Custom form field details within a form version")
public record CustomFormFieldResponse(
        @Schema(description = "Unique identifier of the form field", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the form version this field belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID formVersionId,

        @Schema(description = "Identifier of the section this field belongs to", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID sectionId,

        @Schema(description = "Source type of the field",
                allowableValues = {"CORE_FIELD", "CUSTOM_FIELD", "READONLY_DISPLAY", "INSTRUCTION_TEXT", "SEPARATOR"},
                example = "CUSTOM_FIELD")
        String fieldSource,

        @Schema(description = "Identifier of the custom field definition (when fieldSource is CUSTOM_FIELD)", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID customFieldDefinitionId,

        @Schema(description = "Whether the field is required on this form", example = "false")
        boolean requiredOnForm,

        @Schema(description = "Sort order position of the field within its section", example = "1")
        int sortOrder) {

    public static CustomFormFieldResponse from(CustomFormField f) {
        return new CustomFormFieldResponse(f.id(), f.formVersionId(), f.sectionId(), f.fieldSource(), f.customFieldDefinitionId(), f.requiredOnForm(), f.sortOrder());
    }
}
