package com.company.scopery.modules.externalparty.documentlink.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExternalPartyDocumentLinkRepository {
    ExternalPartyDocumentLink save(ExternalPartyDocumentLink link);
    Optional<ExternalPartyDocumentLink> findById(UUID id);
    List<ExternalPartyDocumentLink> findByOrganizationId(UUID workspaceId, UUID organizationId);
    List<ExternalPartyDocumentLink> findByContactId(UUID workspaceId, UUID contactId);
}
