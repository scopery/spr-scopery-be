package com.company.scopery.modules.configuration.layout.application.response;

import com.company.scopery.modules.configuration.layout.domain.model.LayoutDefinition;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Layout definition details describing how objects are displayed in the UI")
public record LayoutDefinitionResponse(
        @Schema(description = "Unique identifier of the layout definition", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Code of the object type this layout applies to", example = "PROJECT")
        String objectTypeCode,

        @Schema(description = "Type of layout",
                allowableValues = {"DETAIL", "CREATE_FORM", "EDIT_FORM", "PORTAL_DETAIL", "LIST_COLUMNS", "BOARD_CARD"},
                example = "DETAIL")
        String layoutType,

        @Schema(description = "Display name of the layout", example = "Default Project Detail Layout")
        String name,

        @Schema(description = "Current status of the layout definition",
                allowableValues = {"DRAFT", "PUBLISHED", "ARCHIVED"},
                example = "PUBLISHED")
        String status,

        @Schema(description = "Whether this layout is the currently active layout for its type and object type", example = "true")
        boolean currentFlag) {

    public static LayoutDefinitionResponse from(LayoutDefinition l) {
        return new LayoutDefinitionResponse(l.id(), l.objectTypeCode(), l.layoutType(), l.name(), l.status(), l.currentFlag());
    }
}
