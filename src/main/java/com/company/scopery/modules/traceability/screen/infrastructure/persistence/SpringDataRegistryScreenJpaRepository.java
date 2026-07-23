package com.company.scopery.modules.traceability.screen.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryScreenJpaRepository extends JpaRepository<RegistryScreenJpaEntity, UUID> {
    Optional<RegistryScreenJpaEntity> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<RegistryScreenJpaEntity> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
    void deleteByIdAndApplicationId(UUID id, UUID applicationId);
}
