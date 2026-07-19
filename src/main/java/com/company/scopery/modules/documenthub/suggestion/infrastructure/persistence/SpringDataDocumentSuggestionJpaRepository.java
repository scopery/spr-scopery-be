package com.company.scopery.modules.documenthub.suggestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataDocumentSuggestionJpaRepository extends JpaRepository<DocumentSuggestionJpaEntity, UUID> {
    Optional<DocumentSuggestionJpaEntity> findByIdAndDocumentId(UUID id, UUID documentId);
    List<DocumentSuggestionJpaEntity> findByDocumentIdOrderByCreatedAtDesc(UUID documentId);
}
