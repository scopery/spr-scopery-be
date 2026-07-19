package com.company.scopery.modules.traceability.screenaction.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryScreenActionJpaRepository extends JpaRepository<RegistryScreenActionJpaEntity, UUID> {
    Optional<RegistryScreenActionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenActionJpaEntity> findByScreenIdOrderByDisplayOrderAsc(UUID screenId);
}
