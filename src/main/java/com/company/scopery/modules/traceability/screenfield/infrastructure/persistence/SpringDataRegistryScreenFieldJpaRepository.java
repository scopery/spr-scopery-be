package com.company.scopery.modules.traceability.screenfield.infrastructure.persistence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface SpringDataRegistryScreenFieldJpaRepository extends JpaRepository<RegistryScreenFieldJpaEntity, UUID> {
    Optional<RegistryScreenFieldJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenFieldJpaEntity> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM RegistryScreenFieldJpaEntity f WHERE f.id = :id AND f.screenId = :screenId AND f.workspaceId = :workspaceId")
    Optional<RegistryScreenFieldJpaEntity> lockFieldByIdAndScreenIdAndWorkspaceId(
            @Param("id") UUID id,
            @Param("screenId") UUID screenId,
            @Param("workspaceId") UUID workspaceId);
}
