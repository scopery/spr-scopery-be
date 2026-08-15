package com.company.scopery.modules.traceability.componentapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryComponentApiJpaRepository
        extends JpaRepository<RegistryComponentApiJpaEntity, UUID> {

    Optional<RegistryComponentApiJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    boolean existsByComponentIdAndApiIdAndRole(UUID componentId, UUID apiId, String role);
    List<RegistryComponentApiJpaEntity> findByComponentIdOrderByDisplayOrderAsc(UUID componentId);
}
