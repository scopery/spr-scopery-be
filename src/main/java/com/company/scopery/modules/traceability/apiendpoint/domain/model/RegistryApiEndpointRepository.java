package com.company.scopery.modules.traceability.apiendpoint.domain.model;
import java.util.*;
public interface RegistryApiEndpointRepository {
    RegistryApiEndpoint save(RegistryApiEndpoint entity);
    Optional<RegistryApiEndpoint> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<RegistryApiEndpoint> findByApplicationId(UUID applicationId);
}
