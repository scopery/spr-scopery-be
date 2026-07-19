package com.company.scopery.modules.traceability.application.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryApplicationJpaRepository extends JpaRepository<RegistryApplicationJpaEntity, UUID> {
    Optional<RegistryApplicationJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryApplicationJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
