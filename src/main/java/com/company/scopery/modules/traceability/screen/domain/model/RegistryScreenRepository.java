package com.company.scopery.modules.traceability.screen.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface RegistryScreenRepository {
    RegistryScreen save(RegistryScreen entity);
    Optional<RegistryScreen> findByIdAndApplicationId(UUID id, UUID applicationId);
    Optional<RegistryScreen> findById(UUID id);
    List<RegistryScreen> findByIdIn(Collection<UUID> ids);
    List<RegistryScreen> findByApplicationId(UUID applicationId);
    void delete(UUID id, UUID applicationId);
    boolean existsById(UUID id);
}
