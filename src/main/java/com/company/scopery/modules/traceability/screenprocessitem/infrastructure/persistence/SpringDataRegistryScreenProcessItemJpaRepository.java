package com.company.scopery.modules.traceability.screenprocessitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryScreenProcessItemJpaRepository
        extends JpaRepository<RegistryScreenProcessItemJpaEntity, UUID> {

    Optional<RegistryScreenProcessItemJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    List<RegistryScreenProcessItemJpaEntity> findByScreenIdAndStatusOrderByDisplayOrderAsc(UUID screenId, String status);

    List<RegistryScreenProcessItemJpaEntity> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM RegistryScreenProcessItemJpaEntity e WHERE e.id = :id AND e.workspaceId = :workspaceId")
    void deleteByIdAndWorkspaceId(@Param("id") UUID id, @Param("workspaceId") UUID workspaceId);
}
