package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataDocumentRevisionJpaRepository extends JpaRepository<DocumentRevisionJpaEntity, UUID> {
    Optional<DocumentRevisionJpaEntity> findByDocumentIdAndRevisionNo(UUID documentId, long revisionNo);
    Page<DocumentRevisionJpaEntity> findByDocumentIdOrderByRevisionNoDesc(UUID documentId, Pageable pageable);
}
