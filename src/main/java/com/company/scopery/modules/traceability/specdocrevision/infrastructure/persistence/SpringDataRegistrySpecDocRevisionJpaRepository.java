package com.company.scopery.modules.traceability.specdocrevision.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistrySpecDocRevisionJpaRepository
        extends JpaRepository<RegistrySpecDocRevisionJpaEntity, UUID> {

    Optional<RegistrySpecDocRevisionJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    List<RegistrySpecDocRevisionJpaEntity> findByDocumentIdOrderByDisplayOrderAsc(UUID documentId);
}
