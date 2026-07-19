package com.company.scopery.modules.externalparty.address.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExternalPartyAddressRepository {
    ExternalPartyAddress save(ExternalPartyAddress address);
    Optional<ExternalPartyAddress> findById(UUID id);
    List<ExternalPartyAddress> findByOrganizationId(UUID workspaceId, UUID organizationId);
    List<ExternalPartyAddress> findByContactId(UUID workspaceId, UUID contactId);
}
