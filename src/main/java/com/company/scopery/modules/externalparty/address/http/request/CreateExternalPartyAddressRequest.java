package com.company.scopery.modules.externalparty.address.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Request payload for adding an address to an external organization or contact")
public record CreateExternalPartyAddressRequest(
        @Schema(description = "Type of the address (e.g. BILLING, SHIPPING, REGISTERED)", example = "BILLING")
        @NotBlank String addressType,

        @Schema(description = "First line of the address", example = "123 Main Street", nullable = true)
        String line1,

        @Schema(description = "Second line of the address (unit, suite, etc.)", example = "Suite 400", nullable = true)
        String line2,

        @Schema(description = "City", example = "San Francisco", nullable = true)
        String city,

        @Schema(description = "State or region", example = "California", nullable = true)
        String stateRegion,

        @Schema(description = "Postal or ZIP code", example = "94105", nullable = true)
        String postalCode,

        @Schema(description = "ISO 3166-1 alpha-2 country code", example = "US", nullable = true)
        String countryCode,

        @Schema(description = "Whether this is the primary address", example = "true")
        boolean primaryFlag,

        @Schema(description = "ID of the external organization this address belongs to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID externalOrganizationId,

        @Schema(description = "ID of the external contact this address belongs to", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID externalContactId) {}
