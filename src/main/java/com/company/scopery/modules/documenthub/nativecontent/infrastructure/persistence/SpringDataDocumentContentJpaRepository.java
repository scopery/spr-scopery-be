package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataDocumentContentJpaRepository extends JpaRepository<DocumentContentJpaEntity, UUID> {
    Optional<DocumentContentJpaEntity> findByDocumentId(UUID documentId);
}
