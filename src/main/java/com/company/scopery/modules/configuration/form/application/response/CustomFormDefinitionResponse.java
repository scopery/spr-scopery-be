package com.company.scopery.modules.configuration.form.application.response;

import com.company.scopery.modules.configuration.form.domain.model.CustomFormDefinition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Custom form definition details")
public record CustomFormDefinitionResponse(
        @Schema(description = "Unique identifier of the form definition", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique code identifying the form", example = "PROJECT_CREATE_FORM")
        String formCode,

        @Schema(description = "Display name of the form", example = "Project Creation Form")
        String name,

        @Schema(description = "Code of the object type this form is for", example = "PROJECT")
        String objectTypeCode,

        @Schema(description = "Type of the form", example = "CREATE")
        String formType,

        @Schema(description = "Current status of the form definition",
                allowableValues = {"DRAFT", "ACTIVE", "ARCHIVED"},
                example = "ACTIVE")
        String status,

        @Schema(description = "Identifier of the currently active form version", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID currentVersionId) {

    public static CustomFormDefinitionResponse from(CustomFormDefinition f) {
        return new CustomFormDefinitionResponse(f.id(), f.formCode(), f.name(), f.objectTypeCode(), f.formType(), f.status(), f.currentVersionId());
    }
}
