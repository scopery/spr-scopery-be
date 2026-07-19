package com.company.scopery.modules.workspace.organization.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating a new organization")
public record CreateOrganizationRequest(
        @Schema(description = "Display name of the organization", example = "Acme Corporation")
        @NotBlank String name,

        @Schema(description = "Unique short code identifying the organization", example = "ACME")
        @NotBlank String code,

        @Schema(description = "Optional description of the organization", example = "Global leader in anvil manufacturing", nullable = true)
        String description) {
}
