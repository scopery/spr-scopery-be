package com.company.scopery.modules.documenthub.suggestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataDocumentSuggestionOperationJpaRepository extends JpaRepository<DocumentSuggestionOperationJpaEntity, UUID> {
    List<DocumentSuggestionOperationJpaEntity> findBySuggestionIdOrderByOrdinalAsc(UUID suggestionId);
}
