package com.company.scopery.modules.configuration.tag.application.response;

import com.company.scopery.modules.configuration.tag.domain.model.TagDefinition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Tag definition details")
public record TagDefinitionResponse(
        @Schema(description = "Unique identifier of the tag definition", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique code identifying the tag", example = "URGENT")
        String tagCode,

        @Schema(description = "Display label for the tag", example = "Urgent")
        String label,

        @Schema(description = "Display color for the tag (hex color code)", example = "#FF0000", nullable = true)
        String color,

        @Schema(description = "Current status of the tag definition",
                allowableValues = {"ACTIVE", "ARCHIVED"},
                example = "ACTIVE")
        String status) {

    public static TagDefinitionResponse from(TagDefinition t) {
        return new TagDefinitionResponse(t.id(), t.tagCode(), t.label(), t.color(), t.status());
    }
}
