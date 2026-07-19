package com.company.scopery.modules.externalparty.address.domain.model;

import com.company.scopery.modules.externalparty.address.domain.enums.AddressType;
import java.time.Instant;
import java.util.UUID;

public record ExternalPartyAddress(
        UUID id, UUID workspaceId,
        UUID externalOrganizationId, UUID externalContactId,
        AddressType addressType,
        String line1, String line2, String city, String stateRegion,
        String postalCode, String countryCode,
        boolean primaryFlag,
        int version, Instant createdAt, Instant updatedAt) {

    public static ExternalPartyAddress create(UUID workspaceId, UUID organizationId, UUID contactId,
            AddressType type, String line1, String line2, String city,
            String stateRegion, String postalCode, String countryCode, boolean primaryFlag) {
        Instant now = Instant.now();
        return new ExternalPartyAddress(UUID.randomUUID(), workspaceId, organizationId, contactId,
                type, line1, line2, city, stateRegion, postalCode, countryCode, primaryFlag, 0, now, now);
    }
}
