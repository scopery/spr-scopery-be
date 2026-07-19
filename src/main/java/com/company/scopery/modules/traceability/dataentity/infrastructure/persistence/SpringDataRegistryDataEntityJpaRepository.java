package com.company.scopery.modules.traceability.dataentity.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryDataEntityJpaRepository extends JpaRepository<RegistryDataEntityJpaEntity, UUID> {
    List<RegistryDataEntityJpaEntity> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
    Optional<RegistryDataEntityJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
