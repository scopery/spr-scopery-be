package com.company.scopery.modules.aicontext.audit.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAiContextResolutionAuditJpaRepository
        extends JpaRepository<AiContextResolutionAuditJpaEntity, UUID> {

    Page<AiContextResolutionAuditJpaEntity> findByDocumentId(UUID documentId, Pageable pageable);
}
