package com.company.scopery.modules.configuration.statusset.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to add a status value to a status set")
public record CreateStatusValueRequest(
        @Schema(description = "Unique code identifying the status value within its set", example = "IN_PROGRESS")
        @NotBlank String valueCode,

        @Schema(description = "Display label for the status value", example = "In Progress")
        @NotBlank String label,

        @Schema(description = "Domain category grouping this status (e.g. OPEN, IN_PROGRESS, DONE)", example = "IN_PROGRESS")
        @NotBlank String domainCategory,

        @Schema(description = "Sort order position of the status value within the set", example = "1", nullable = true)
        Integer sortOrder) {}
