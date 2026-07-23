package com.company.scopery.modules.traceability.appcomponent.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryAppComponentJpaRepository extends JpaRepository<RegistryAppComponentJpaEntity, UUID> {
    List<RegistryAppComponentJpaEntity> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
    Optional<RegistryAppComponentJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
