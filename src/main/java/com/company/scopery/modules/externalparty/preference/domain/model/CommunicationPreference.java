package com.company.scopery.modules.externalparty.preference.domain.model;

import java.time.Instant;
import java.util.UUID;

public record CommunicationPreference(
        UUID id, UUID workspaceId,
        UUID externalOrganizationId, UUID externalContactId,
        String preferredChannelType, String preferredLanguage,
        boolean doNotContact, String notes,
        int version, Instant createdAt, Instant updatedAt) {

    public static CommunicationPreference create(UUID workspaceId, UUID organizationId, UUID contactId,
            String preferredChannelType, String preferredLanguage, boolean doNotContact, String notes) {
        Instant now = Instant.now();
        return new CommunicationPreference(UUID.randomUUID(), workspaceId, organizationId, contactId,
                preferredChannelType, preferredLanguage, doNotContact, notes, 0, now, now);
    }

    public CommunicationPreference update(String preferredChannelType, String preferredLanguage,
            boolean doNotContact, String notes) {
        return new CommunicationPreference(id, workspaceId, externalOrganizationId, externalContactId,
                preferredChannelType, preferredLanguage, doNotContact, notes, version, createdAt, Instant.now());
    }
}
