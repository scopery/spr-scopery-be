package com.company.scopery.modules.servicesupport.serviceprofile.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceProfileRepository {
    ServiceProfile save(ServiceProfile profile);
    Optional<ServiceProfile> findById(UUID id);
    List<ServiceProfile> findByWorkspaceId(UUID workspaceId);
}
