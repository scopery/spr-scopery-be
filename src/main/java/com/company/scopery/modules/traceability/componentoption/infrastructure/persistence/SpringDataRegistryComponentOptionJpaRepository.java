package com.company.scopery.modules.traceability.componentoption.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryComponentOptionJpaRepository
        extends JpaRepository<RegistryComponentOptionJpaEntity, UUID> {

    Optional<RegistryComponentOptionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    List<RegistryComponentOptionJpaEntity> findByComponentIdOrderByDisplayOrderAsc(UUID componentId);

    List<RegistryComponentOptionJpaEntity> findByComponentIdInOrderByDisplayOrderAsc(Iterable<UUID> componentIds);

    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);

    boolean existsByComponentIdAndOptionValue(UUID componentId, String optionValue);
}
