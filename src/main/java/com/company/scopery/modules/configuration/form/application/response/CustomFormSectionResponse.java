package com.company.scopery.modules.configuration.form.application.response;

import com.company.scopery.modules.configuration.form.domain.model.CustomFormSection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Custom form section details grouping fields within a form version")
public record CustomFormSectionResponse(
        @Schema(description = "Unique identifier of the form section", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the form version this section belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID formVersionId,

        @Schema(description = "Display title of the section", example = "Basic Information")
        String title,

        @Schema(description = "Sort order position of the section within the form", example = "1")
        int sortOrder) {

    public static CustomFormSectionResponse from(CustomFormSection s) {
        return new CustomFormSectionResponse(s.id(), s.formVersionId(), s.title(), s.sortOrder());
    }
}
