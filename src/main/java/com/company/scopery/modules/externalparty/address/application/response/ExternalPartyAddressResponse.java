package com.company.scopery.modules.externalparty.address.application.response;

import com.company.scopery.modules.externalparty.address.domain.model.ExternalPartyAddress;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Representation of an address for an external organization or contact")
public record ExternalPartyAddressResponse(
        @Schema(description = "Unique identifier of the address record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace this address belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "ID of the external organization this address belongs to", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID externalOrganizationId,

        @Schema(description = "ID of the external contact this address belongs to", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID externalContactId,

        @Schema(description = "Type of the address (e.g. BILLING, SHIPPING, REGISTERED)", example = "BILLING", nullable = true)
        String addressType,

        @Schema(description = "First line of the address", example = "123 Main Street", nullable = true)
        String line1,

        @Schema(description = "Second line of the address", example = "Suite 400", nullable = true)
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
        boolean primaryFlag) {

    public static ExternalPartyAddressResponse from(ExternalPartyAddress d) {
        return new ExternalPartyAddressResponse(d.id(), d.workspaceId(),
                d.externalOrganizationId(), d.externalContactId(),
                d.addressType() != null ? d.addressType().name() : null,
                d.line1(), d.line2(), d.city(), d.stateRegion(),
                d.postalCode(), d.countryCode(), d.primaryFlag());
    }
}
