package com.company.scopery.modules.configuration.fieldoption.application.response;

import com.company.scopery.modules.configuration.fieldoption.domain.model.CustomFieldOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Custom field option details")
public record CustomFieldOptionResponse(
        @Schema(description = "Unique identifier of the field option", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the custom field definition this option belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID customFieldDefinitionId,

        @Schema(description = "Unique code for the option", example = "HIGH")
        String optionCode,

        @Schema(description = "Display label for the option", example = "High")
        String label,

        @Schema(description = "Sort order position of the option", example = "1")
        int sortOrder,

        @Schema(description = "Current status of the option",
                allowableValues = {"ACTIVE", "ARCHIVED"},
                example = "ACTIVE")
        String status) {

    public static CustomFieldOptionResponse from(CustomFieldOption o) {
        return new CustomFieldOptionResponse(o.id(), o.customFieldDefinitionId(), o.optionCode(), o.label(), o.sortOrder(), o.status());
    }
}
