package com.company.scopery.modules.configuration.statusset.application.response;

import com.company.scopery.modules.configuration.statusset.domain.model.StatusSet;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Status set details defining a collection of status values for an object type")
public record StatusSetResponse(
        @Schema(description = "Unique identifier of the status set", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Code of the object type this status set applies to", example = "PROJECT")
        String objectTypeCode,

        @Schema(description = "Unique code identifying the status set", example = "PROJECT_LIFECYCLE")
        String setCode,

        @Schema(description = "Display name of the status set", example = "Project Lifecycle")
        String name,

        @Schema(description = "Current status of the status set",
                allowableValues = {"ACTIVE", "ARCHIVED"},
                example = "ACTIVE")
        String status) {

    public static StatusSetResponse from(StatusSet s) {
        return new StatusSetResponse(s.id(), s.objectTypeCode(), s.setCode(), s.name(), s.status());
    }
}
