package com.company.scopery.modules.externalparty.address.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataExternalPartyAddressJpaRepository extends JpaRepository<ExternalPartyAddressJpaEntity, UUID> {
    List<ExternalPartyAddressJpaEntity> findByWorkspaceIdAndExternalOrganizationId(UUID workspaceId, UUID organizationId);
    List<ExternalPartyAddressJpaEntity> findByWorkspaceIdAndExternalContactId(UUID workspaceId, UUID contactId);
}
