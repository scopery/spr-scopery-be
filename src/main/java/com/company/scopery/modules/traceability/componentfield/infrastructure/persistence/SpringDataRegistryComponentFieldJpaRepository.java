package com.company.scopery.modules.traceability.componentfield.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryComponentFieldJpaRepository
        extends JpaRepository<RegistryComponentFieldJpaEntity, UUID> {

    Optional<RegistryComponentFieldJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    boolean existsByComponentIdAndFieldKey(UUID componentId, String fieldKey);

    List<RegistryComponentFieldJpaEntity> findByComponentIdOrderByDisplayOrderAsc(UUID componentId);
}
