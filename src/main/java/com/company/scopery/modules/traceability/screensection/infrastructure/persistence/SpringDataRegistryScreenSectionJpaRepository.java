package com.company.scopery.modules.traceability.screensection.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryScreenSectionJpaRepository extends JpaRepository<RegistryScreenSectionJpaEntity, UUID> {
    Optional<RegistryScreenSectionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenSectionJpaEntity> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
