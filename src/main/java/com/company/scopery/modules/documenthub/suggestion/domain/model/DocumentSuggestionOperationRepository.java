package com.company.scopery.modules.documenthub.suggestion.domain.model;

import java.util.List;
import java.util.UUID;

public interface DocumentSuggestionOperationRepository {
    void saveAll(List<DocumentSuggestionOperation> operations);
    List<DocumentSuggestionOperation> findBySuggestionId(UUID suggestionId);
}
