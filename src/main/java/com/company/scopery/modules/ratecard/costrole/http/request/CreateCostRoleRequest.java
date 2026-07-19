package com.company.scopery.modules.ratecard.costrole.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Request body for creating a new cost role")
public record CreateCostRoleRequest(
        @Schema(description = "Unique code identifying the cost role", example = "SENIOR_DEV")
        @NotBlank String code,

        @Schema(description = "Display name of the cost role", example = "Senior Developer")
        @NotBlank String name,

        @Schema(description = "Optional description of the cost role", example = "Leads technical design and implementation", nullable = true)
        String description,

        @Schema(description = "Scope level of the cost role", example = "SYSTEM", allowableValues = {"SYSTEM", "ORGANIZATION", "WORKSPACE"})
        @NotBlank String scope,

        @Schema(description = "Organization this cost role belongs to (required for ORGANIZATION scope)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace this cost role belongs to (required for WORKSPACE scope)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID workspaceId,

        @Schema(description = "Optional category grouping for the cost role", example = "Engineering", nullable = true)
        String category
) {}
