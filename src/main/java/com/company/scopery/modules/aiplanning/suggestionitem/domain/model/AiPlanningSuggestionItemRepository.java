package com.company.scopery.modules.aiplanning.suggestionitem.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiPlanningSuggestionItemRepository {
    AiPlanningSuggestionItem save(AiPlanningSuggestionItem item);
    Optional<AiPlanningSuggestionItem> findById(UUID id);
    Optional<AiPlanningSuggestionItem> findByIdAndSuggestionId(UUID id, UUID suggestionId);
    List<AiPlanningSuggestionItem> findBySuggestionId(UUID suggestionId);
}
