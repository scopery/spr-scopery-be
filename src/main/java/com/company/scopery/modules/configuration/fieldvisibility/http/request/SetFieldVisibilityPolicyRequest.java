package com.company.scopery.modules.configuration.fieldvisibility.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload to set the visibility policy for a custom field for a given audience")
public record SetFieldVisibilityPolicyRequest(
        @Schema(description = "Type of audience this policy applies to", example = "CLIENT")
        @NotBlank String audienceType,

        @Schema(description = "Whether the field should be visible to the specified audience", example = "true")
        @NotNull Boolean visible) {}
