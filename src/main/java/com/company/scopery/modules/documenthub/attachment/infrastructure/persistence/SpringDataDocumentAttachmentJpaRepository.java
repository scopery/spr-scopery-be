package com.company.scopery.modules.documenthub.attachment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataDocumentAttachmentJpaRepository extends JpaRepository<DocumentAttachmentJpaEntity, UUID> {
    Optional<DocumentAttachmentJpaEntity> findByIdAndDocumentId(UUID id, UUID documentId);
    List<DocumentAttachmentJpaEntity> findByDocumentIdOrderByCreatedAtAsc(UUID documentId);
}
