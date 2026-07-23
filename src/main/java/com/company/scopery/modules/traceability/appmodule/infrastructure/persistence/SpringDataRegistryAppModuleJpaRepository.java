package com.company.scopery.modules.traceability.appmodule.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRegistryAppModuleJpaRepository extends JpaRepository<RegistryAppModuleJpaEntity, UUID> {
    List<RegistryAppModuleJpaEntity> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
    Optional<RegistryAppModuleJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    void deleteByIdAndWorkspaceId(UUID id, UUID workspaceId);
}
