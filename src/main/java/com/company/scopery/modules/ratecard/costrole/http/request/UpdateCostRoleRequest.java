package com.company.scopery.modules.ratecard.costrole.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for updating an existing cost role")
public record UpdateCostRoleRequest(
        @Schema(description = "Updated display name of the cost role", example = "Senior Software Developer")
        @NotBlank String name,

        @Schema(description = "Updated description of the cost role", example = "Leads technical design and implementation", nullable = true)
        String description,

        @Schema(description = "Updated category grouping for the cost role", example = "Engineering", nullable = true)
        String category
) {}
