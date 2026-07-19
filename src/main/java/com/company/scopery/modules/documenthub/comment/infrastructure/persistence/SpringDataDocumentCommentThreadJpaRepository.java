package com.company.scopery.modules.documenthub.comment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataDocumentCommentThreadJpaRepository extends JpaRepository<DocumentCommentThreadJpaEntity, UUID> {
    Optional<DocumentCommentThreadJpaEntity> findByIdAndDocumentId(UUID id, UUID documentId);
    List<DocumentCommentThreadJpaEntity> findByDocumentIdOrderByCreatedAtAsc(UUID documentId);
    List<DocumentCommentThreadJpaEntity> findByDocumentIdAndStatusOrderByCreatedAtAsc(UUID documentId, String status);
}
