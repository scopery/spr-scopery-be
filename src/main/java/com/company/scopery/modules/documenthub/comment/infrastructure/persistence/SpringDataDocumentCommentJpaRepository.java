package com.company.scopery.modules.documenthub.comment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataDocumentCommentJpaRepository extends JpaRepository<DocumentCommentJpaEntity, UUID> {
    List<DocumentCommentJpaEntity> findByThreadIdOrderByCreatedAtAsc(UUID threadId);
}
