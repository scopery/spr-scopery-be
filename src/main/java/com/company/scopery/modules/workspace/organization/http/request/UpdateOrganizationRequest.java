package com.company.scopery.modules.workspace.organization.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for updating an existing organization")
public record UpdateOrganizationRequest(
        @Schema(description = "New display name of the organization", example = "Acme Corporation")
        @NotBlank String name,

        @Schema(description = "Updated description of the organization", example = "Global leader in anvil manufacturing", nullable = true)
        String description) {
}
