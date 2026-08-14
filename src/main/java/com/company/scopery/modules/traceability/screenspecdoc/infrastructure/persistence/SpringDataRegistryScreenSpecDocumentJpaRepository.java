package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryScreenSpecDocumentJpaRepository
        extends JpaRepository<RegistryScreenSpecDocumentJpaEntity, UUID> {

    Optional<RegistryScreenSpecDocumentJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    Optional<RegistryScreenSpecDocumentJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);

    boolean existsByProjectIdAndDocumentCode(UUID projectId, String documentCode);

    List<RegistryScreenSpecDocumentJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    List<RegistryScreenSpecDocumentJpaEntity> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);
}
