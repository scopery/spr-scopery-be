package com.company.scopery.modules.traceability.dataentityfield.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryDataEntityFieldJpaRepository extends JpaRepository<RegistryDataEntityFieldJpaEntity, UUID> {
    Optional<RegistryDataEntityFieldJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryDataEntityFieldJpaEntity> findByEntityIdOrderByDisplayOrderAsc(UUID entityId);
    List<RegistryDataEntityFieldJpaEntity> findByIdIn(Collection<UUID> ids);
    boolean existsByEntityIdAndColumnName(UUID entityId, String columnName);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
