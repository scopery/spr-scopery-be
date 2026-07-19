package com.company.scopery.modules.configuration.layout.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to create a new layout definition")
public record CreateLayoutRequest(
        @Schema(description = "Code of the object type this layout applies to", example = "PROJECT")
        @NotBlank String objectTypeCode,

        @Schema(description = "Type of layout",
                allowableValues = {"DETAIL", "CREATE_FORM", "EDIT_FORM", "PORTAL_DETAIL", "LIST_COLUMNS", "BOARD_CARD"},
                example = "DETAIL")
        @NotBlank String layoutType,

        @Schema(description = "Display name of the layout", example = "Default Project Detail Layout")
        @NotBlank String name,

        @Schema(description = "JSON string defining the layout structure and field arrangement", example = "{\"columns\":[{\"fieldKey\":\"name\",\"width\":200}]}")
        @NotBlank String layoutJson) {}
