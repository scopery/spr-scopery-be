package com.company.scopery.modules.traceability.apiendpoint.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface RegistryApiEndpointRepository {
    RegistryApiEndpoint save(RegistryApiEndpoint entity);
    Optional<RegistryApiEndpoint> findById(UUID id);
    List<RegistryApiEndpoint> findByIdIn(Collection<UUID> ids);
    Optional<RegistryApiEndpoint> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<RegistryApiEndpoint> findByApplicationId(UUID applicationId);
    void delete(UUID id, UUID applicationId);
}
