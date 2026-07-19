package com.company.scopery.modules.externalparty.preference.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCommunicationPreferenceJpaRepository extends JpaRepository<CommunicationPreferenceJpaEntity, UUID> {
    Optional<CommunicationPreferenceJpaEntity> findByWorkspaceIdAndExternalOrganizationId(UUID workspaceId, UUID organizationId);
    Optional<CommunicationPreferenceJpaEntity> findByWorkspaceIdAndExternalContactId(UUID workspaceId, UUID contactId);
}
