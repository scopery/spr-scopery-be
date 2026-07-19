package com.company.scopery.modules.ratecard.ratecard.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Request body for creating a new rate card")
public record CreateRateCardRequest(
        @Schema(description = "Unique code identifying the rate card", example = "RC_GLOBAL_2026")
        @NotBlank String code,

        @Schema(description = "Display name of the rate card", example = "Global Rate Card 2026")
        @NotBlank String name,

        @Schema(description = "Optional description of the rate card", example = "Standard global billing rates for 2026", nullable = true)
        String description,

        @Schema(description = "Scope level of the rate card", example = "SYSTEM", allowableValues = {"SYSTEM", "ORGANIZATION", "WORKSPACE", "CLIENT", "PROJECT"})
        @NotBlank String scope,

        @Schema(description = "Organization this rate card belongs to (required for ORGANIZATION scope)", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID organizationId,

        @Schema(description = "Workspace this rate card belongs to (required for WORKSPACE scope)", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID workspaceId,

        @Schema(description = "Client this rate card belongs to (required for CLIENT scope)", example = "550e8400-e29b-41d4-a716-446655440006", nullable = true)
        UUID clientId,

        @Schema(description = "Project this rate card belongs to (required for PROJECT scope)", example = "550e8400-e29b-41d4-a716-446655440007", nullable = true)
        UUID projectId,

        @Schema(description = "Default currency code for the rate card following ISO 4217", example = "USD")
        @NotBlank String defaultCurrencyCode,

        @Schema(description = "Whether this should be the default rate card for its scope (null treated as false)", example = "false", nullable = true)
        Boolean isDefault
) {}
