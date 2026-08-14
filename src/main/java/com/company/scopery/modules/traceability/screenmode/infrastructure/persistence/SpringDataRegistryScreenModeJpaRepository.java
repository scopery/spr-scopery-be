package com.company.scopery.modules.traceability.screenmode.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryScreenModeJpaRepository extends JpaRepository<RegistryScreenModeJpaEntity, UUID> {
    Optional<RegistryScreenModeJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenModeJpaEntity> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);
    List<RegistryScreenModeJpaEntity> findByIdIn(Collection<UUID> ids);
    Optional<RegistryScreenModeJpaEntity> findByScreenIdAndModeCode(UUID screenId, String modeCode);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
