package com.company.scopery.modules.externalparty.address.application.command;

import java.util.UUID;

public record CreateExternalPartyAddressCommand(
        UUID workspaceId,
        UUID externalOrganizationId,
        UUID externalContactId,
        String addressType,
        String line1, String line2, String city,
        String stateRegion, String postalCode, String countryCode,
        boolean primaryFlag) {}
