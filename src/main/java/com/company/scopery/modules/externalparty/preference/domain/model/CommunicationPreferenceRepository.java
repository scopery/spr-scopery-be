package com.company.scopery.modules.externalparty.preference.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface CommunicationPreferenceRepository {
    CommunicationPreference save(CommunicationPreference preference);
    Optional<CommunicationPreference> findByOrganizationId(UUID workspaceId, UUID organizationId);
    Optional<CommunicationPreference> findByContactId(UUID workspaceId, UUID contactId);
}
