package com.company.scopery.modules.externalparty.organization.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating a new external organization")
public record CreateExternalOrganizationRequest(
        @Schema(description = "Optional unique short code identifying the external organization", example = "ACME-EXT", nullable = true)
        String code,

        @Schema(description = "Display name of the external organization", example = "Acme Corp External")
        @NotBlank String name,

        @Schema(description = "Type classification of the external organization", example = "CLIENT", allowableValues = {"CLIENT", "VENDOR", "PARTNER", "REGULATOR"})
        @NotBlank String organizationType,

        @Schema(description = "Tax identification number of the external organization", example = "12-3456789", nullable = true)
        String taxId,

        @Schema(description = "Website URL of the external organization", example = "https://www.acme.com", nullable = true)
        String website,

        @Schema(description = "Additional notes about the external organization", example = "Primary client for Project Alpha", nullable = true)
        String notes) {}
