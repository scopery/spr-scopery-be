package com.company.scopery.modules.configuration.statusset.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to create a new status set for an object type")
public record CreateStatusSetRequest(
        @Schema(description = "Code of the object type this status set applies to", example = "PROJECT")
        @NotBlank String objectTypeCode,

        @Schema(description = "Unique code identifying the status set", example = "PROJECT_LIFECYCLE")
        @NotBlank String setCode,

        @Schema(description = "Display name of the status set", example = "Project Lifecycle")
        @NotBlank String name) {}
