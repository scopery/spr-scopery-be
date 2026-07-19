package com.company.scopery.modules.traceability.apiendpoint.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryApiEndpointJpaRepository extends JpaRepository<RegistryApiEndpointJpaEntity, UUID> {
    Optional<RegistryApiEndpointJpaEntity> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<RegistryApiEndpointJpaEntity> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
}
