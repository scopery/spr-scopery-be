package com.company.scopery.modules.externalparty.documentlink.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ExternalPartyDocumentLink(
        UUID id, UUID workspaceId,
        UUID externalOrganizationId, UUID externalContactId,
        UUID documentId, String linkNote,
        int version, Instant createdAt, Instant updatedAt) {

    public static ExternalPartyDocumentLink create(UUID workspaceId, UUID organizationId, UUID contactId,
            UUID documentId, String linkNote) {
        Instant now = Instant.now();
        return new ExternalPartyDocumentLink(UUID.randomUUID(), workspaceId, organizationId, contactId,
                documentId, linkNote, 0, now, now);
    }
}
