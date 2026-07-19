package com.company.scopery.modules.configuration.fieldoption.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to create a new custom field option")
public record CreateFieldOptionRequest(
        @Schema(description = "Unique code for the option", example = "HIGH")
        @NotBlank String optionCode,

        @Schema(description = "Display label for the option", example = "High")
        @NotBlank String label,

        @Schema(description = "Sort order position of the option", example = "1", nullable = true)
        Integer sortOrder) {}
