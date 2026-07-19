package com.company.scopery.modules.configuration.statusset.application.response;

import com.company.scopery.modules.configuration.statusset.domain.model.StatusValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Status value details representing a single status within a status set")
public record StatusValueResponse(
        @Schema(description = "Unique identifier of the status value", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the status set this value belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID statusSetId,

        @Schema(description = "Unique code identifying the status value within its set", example = "IN_PROGRESS")
        String valueCode,

        @Schema(description = "Display label for the status value", example = "In Progress")
        String label,

        @Schema(description = "Domain category grouping this status (e.g. OPEN, IN_PROGRESS, DONE)", example = "IN_PROGRESS", nullable = true)
        String domainCategory) {

    public static StatusValueResponse from(StatusValue v) {
        return new StatusValueResponse(v.id(), v.statusSetId(), v.valueCode(), v.label(), v.domainCategory());
    }
}
