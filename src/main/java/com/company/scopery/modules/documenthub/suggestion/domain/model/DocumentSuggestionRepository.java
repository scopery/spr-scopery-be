package com.company.scopery.modules.documenthub.suggestion.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentSuggestionRepository {
    DocumentSuggestion save(DocumentSuggestion suggestion);
    Optional<DocumentSuggestion> findByIdAndDocumentId(UUID id, UUID documentId);
    List<DocumentSuggestion> findByDocumentId(UUID documentId);
}
