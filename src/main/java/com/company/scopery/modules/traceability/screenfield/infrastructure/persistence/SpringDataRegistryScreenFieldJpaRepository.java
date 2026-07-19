package com.company.scopery.modules.traceability.screenfield.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryScreenFieldJpaRepository extends JpaRepository<RegistryScreenFieldJpaEntity, UUID> {
    Optional<RegistryScreenFieldJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenFieldJpaEntity> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);
}
