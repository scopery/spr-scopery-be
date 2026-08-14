package com.company.scopery.modules.traceability.screeneventitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryScreenEventItemJpaRepository
        extends JpaRepository<RegistryScreenEventItemJpaEntity, UUID> {

    Optional<RegistryScreenEventItemJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    List<RegistryScreenEventItemJpaEntity> findByScreenIdAndStatusOrderByDisplayOrderAsc(UUID screenId, String status);

    List<RegistryScreenEventItemJpaEntity> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM RegistryScreenEventItemJpaEntity e WHERE e.id = :id AND e.workspaceId = :workspaceId")
    void deleteByIdAndWorkspaceId(@Param("id") UUID id, @Param("workspaceId") UUID workspaceId);
}
