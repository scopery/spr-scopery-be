package com.company.scopery.modules.externalparty.documentlink.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataExternalPartyDocumentLinkJpaRepository extends JpaRepository<ExternalPartyDocumentLinkJpaEntity, UUID> {
    List<ExternalPartyDocumentLinkJpaEntity> findByWorkspaceIdAndExternalOrganizationId(UUID workspaceId, UUID organizationId);
    List<ExternalPartyDocumentLinkJpaEntity> findByWorkspaceIdAndExternalContactId(UUID workspaceId, UUID contactId);
}
