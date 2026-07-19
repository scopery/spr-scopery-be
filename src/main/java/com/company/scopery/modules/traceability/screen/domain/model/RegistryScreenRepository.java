package com.company.scopery.modules.traceability.screen.domain.model;
import java.util.*;
public interface RegistryScreenRepository {
    RegistryScreen save(RegistryScreen entity);
    Optional<RegistryScreen> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<RegistryScreen> findByApplicationId(UUID applicationId);
}
