package com.company.scopery.modules.configuration.tag.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to create a new tag definition")
public record CreateTagRequest(
        @Schema(description = "Unique code identifying the tag", example = "URGENT")
        @NotBlank String tagCode,

        @Schema(description = "Display label for the tag", example = "Urgent")
        @NotBlank String label,

        @Schema(description = "Display color for the tag (hex color code)", example = "#FF0000", nullable = true)
        String color,

        @Schema(description = "JSON array of object type codes this tag is allowed to be used on", example = "[\"PROJECT\",\"TASK\"]", nullable = true)
        String allowedObjectTypesJson) {}
